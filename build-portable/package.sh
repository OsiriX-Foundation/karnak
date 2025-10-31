#!/bin/bash
# This script makes the multiplatform build of Karnak with jpackage
#
# Initial script by Nicolas Roduit

# Build Parameters
REVISON_INC="1"
PACKAGE=NO

# Options
NAME="Karnak"
IDENTIFIER="org.karnak.launcher"

# Aux functions:
die ( ) {
  echo
  echo -e "ERROR: $*"
  exit 1
}

POSITIONAL=()
while [[ $# -gt 0 ]]
do
  key="$1"

  case $key in
    -h|--help)
echo "Usage: package.sh <options>"
echo "Sample usages:"
echo "    Build an installer for the current platform with the minimal required parameters"
echo "        package.sh --jdk /home/user/jdk-21"
echo ""
echo "Options:"
echo " --help -h
Print the usage text with a list and description of each valid
option the output stream, and exit"
echo " --input -i
Path of the karnak-native directory"
echo " --output -o
Path of the base output directory.
Default value is the current directory"
echo " --jdk -j
Path of the jdk with the jpackage module"
echo " --temp
Path of the temporary directory during build"
echo " --installer
Build also an installer"
echo " --mac-signing-key-user-name
Key user name of the certificate to sign the bundle"
exit 0
;;
-j|--jdk)
JDK_PATH_UNIX="$2"
shift # past argument
shift # past value
;;
-i|--input)
INPUT_PATH="$2"
shift # past argument
shift # past value
;;
-o|--output)
OUTPUT_PATH="$2"
shift # past argument
shift # past value
;;
--temp)
TEMP_PATH="$2"
shift # past argument
shift # past value
;;
--installer)
PACKAGE="YES"
shift # past argument
;;
--mac-signing-key-user-name)
CERTIFICATE="$2"
shift # past argument
shift # past value
;;
*)    # unknown option
POSITIONAL+=("$1") # save it in an array for later
shift # past argument
;;
esac
done
set -- "${POSITIONAL[@]}" # restore positional parameters


curPath=$(dirname "$(readlink -f "$0")")
rootdir="$(dirname "$curPath")"


echo "rootdir: $rootdir"

if [ ! -d "${INPUT_PATH}" ] ; then
  INPUT_PATH="${rootdir}/target"
fi

if [ ! -d "${INPUT_PATH}" ] ; then
  die "The input path ${INPUT_PATH} doesn't exist, provide a valid value for --input"
fi

# Detect ARC_OS from the unique folder in `INPUT_PATH/classes/lib`
lib_dir="$INPUT_PATH/classes/lib"
if [ ! -d "$lib_dir" ]; then
  die "The library path $lib_dir doesn't exist, provide a valid value for --input"
fi

# Collect immediate subdirectories (folder names only)
mapfile -t _subdirs < <(find "$lib_dir" -mindepth 1 -maxdepth 1 -type d -printf '%f\n' 2>/dev/null || true)

if [ ${#_subdirs[@]} -eq 1 ]; then
  ARC_OS="${_subdirs[0]}"
else
  ARC_OS=""
  for cand in "${_subdirs[@]}"; do
    case "$cand" in
      *windows*) ARC_OS="$cand"; break;;
      *macosx*) ARC_OS="$cand"; break;;
      *linux*) ARC_OS="$cand"; break;;
    esac
  done
  # fallback to first if nothing matched
  if [ -z "$ARC_OS" ] && [ ${#_subdirs[@]} -gt 0 ]; then
    ARC_OS="${_subdirs[0]}"
  fi
fi

if [ -z "$ARC_OS" ] ; then
  die "Cannot get Java system architecture from $lib_dir"
fi
machine=$(echo "${ARC_OS}" | cut -d'-' -f1)
arc=$(echo "${ARC_OS}" | cut -d'-' -f2-3)

echo "Platform: $machine"

if [ "$machine" = "windows" ] ; then
  INPUT_PATH_UNIX=$(cygpath -u "$INPUT_PATH")
  OUTPUT_PATH_UNIX=$(cygpath -u "$OUTPUT_PATH")
  RES="${curPath}\resources\\${machine}"
else
  INPUT_PATH_UNIX="$INPUT_PATH"
  OUTPUT_PATH_UNIX="$OUTPUT_PATH"
  RES="${curPath}/resources/$machine"
fi

# Set custom JDK path (>= JDK 11)
export JAVA_HOME=$JDK_PATH_UNIX

echo "System: ${ARC_OS}"
echo "JDK path: ${JDK_PATH_UNIX}"
echo "Karnak version: ${KARNAK_VERSION}"
echo "Input path: ${INPUT_PATH}"
if [ "$machine" = "windows" ]
then
  echo "Input unix path: ${INPUT_PATH_UNIX}"
fi

# Specify the required Java version.
# Only major version is checked. Minor version or any other version string info is left out.
REQUIRED_TEXT_VERSION="${JAVA_VERSION}"
# Extract major version number for comparisons from the required version string.
# In order to do that, remove leading "1." if exists, and minor and security versions.
REQUIRED_MAJOR_VERSION=$(echo "$REQUIRED_TEXT_VERSION" | sed -e 's/^1\.//' -e 's/\..*//')

# Check jlink command.
if [ -x "$JDK_PATH_UNIX/bin/jpackage" ] ; then
  JPKGCMD="$JDK_PATH_UNIX/bin/jpackage"
  JAVACMD="$JDK_PATH_UNIX/bin/java"
else
  die "JAVA_HOME is not set and no 'jpackage' command could be found in your PATH. Specify a jdk path >=$REQUIRED_TEXT_VERSION."
fi

# Then, get the installed version
INSTALLED_VERSION=$($JAVACMD -version 2>&1 | awk '/version [0-9]*/ {print $3;}')
echo "Found java version $INSTALLED_VERSION"
echo "Java command path: $JAVACMD"

# Remove double quotes, remove leading "1." if it exists and remove everything apart from the major version number.
INSTALLED_MAJOR_VERSION=$(echo "$INSTALLED_VERSION" | sed -e 's/"//g' -e 's/^1\.//' -e 's/\..*//' -e 's/-.*//')
echo "Java major version: $INSTALLED_MAJOR_VERSION"
if (( INSTALLED_MAJOR_VERSION < REQUIRED_MAJOR_VERSION )) ; then
  die "Your version of java is too low to run this script.\nPlease update to $REQUIRED_TEXT_VERSION or higher"
fi

if [ -z "$OUTPUT_PATH" ] ; then
  OUTPUT_PATH="target/karnak-$ARC_OS-jdk$INSTALLED_MAJOR_VERSION-$KARNAK_VERSION"
  OUTPUT_PATH_UNIX="$OUTPUT_PATH"
fi


if [ "$machine" = "windows" ] ; then
  INPUT_DIR="$INPUT_PATH\portable"
  IMAGE_PATH="$OUTPUT_PATH\\${NAME}"
else
  IMAGE_PATH="$OUTPUT_PATH/$NAME"
  INPUT_DIR="$INPUT_PATH_UNIX/portable"
fi

KARNAK_CLEAN_VERSION=$(echo "$KARNAK_VERSION" | sed -e 's/"//g' -e 's/-.*//' -e 's/\(\([0-9]\+\.\)\{2\}[0-9]\+\)\.[0-9]\+/\1/')


# Remove previous package
if [ -d "${OUTPUT_PATH}" ] ; then
  rm -rf "${OUTPUT_PATH}"
fi

if [ -z "$TEMP_PATH" ] ; then
  declare -a tmpArgs=()
else
  declare -a tmpArgs=("--temp" "$TEMP_PATH")
fi

if [ -d "${TEMP_PATH}" ] ; then
  rm -rf "${TEMP_PATH}"
fi

if [ "$machine" = "macosx" ] ; then
  if [[ -n "$CERTIFICATE" ]] ; then
    declare -a signArgs=("--mac-package-identifier" "$IDENTIFIER" "--mac-signing-key-user-name" "$CERTIFICATE"  "--mac-sign")
  else
    declare -a signArgs=("--mac-package-identifier" "$IDENTIFIER")
  fi
else
  declare -a signArgs=()
fi
declare -a commonOptions=(
"--java-options" "-Dspring.profiles.active=jpackage" \
"--java-options" "-Djava.library.path=\$APPDIR/dicom-opencv" \
"--java-options" "--enable-native-access=ALL-UNNAMED");

mkdir -p "$INPUT_PATH_UNIX/portable/dicom-opencv"
cp "$INPUT_PATH_UNIX/karnak-${KARNAK_VERSION}".jar "$INPUT_PATH_UNIX/portable/karnak-${KARNAK_VERSION}.jar"
cp -r "$INPUT_PATH_UNIX/classes/lib/${ARC_OS}"/* "$INPUT_PATH_UNIX/portable/dicom-opencv/"

$JPKGCMD --type app-image --input "$INPUT_DIR" --dest "$OUTPUT_PATH" --name "$NAME" \
--main-jar karnak-"${KARNAK_VERSION}".jar --main-class org.springframework.boot.loader.launch.JarLauncher \
--module-path "$JDK_PATH_UNIX/jmods" --add-modules ALL-MODULE-PATH \
--resource-dir "$RES" --app-version "$KARNAK_CLEAN_VERSION" \
"${tmpArgs[@]}" --verbose "${signArgs[@]}" "${commonOptions[@]}"

if [ "$machine" = "macosx" ] && [[ -n "$CERTIFICATE" ]] ; then
    codesign --timestamp --entitlements "$RES/uri-launcher.entitlements" --options runtime --force -vvv --sign "$CERTIFICATE" "$RES/$NAME.app"
fi

if [ "$PACKAGE" = "YES" ] ; then
  VENDOR="Karnak Team"
  COPYRIGHT="© 2009-2025 Karnak Team"
  if [ "$machine" = "windows" ] ; then
    [ "$arc" = "aarch64" ]  && UPGRADE_UID="d1aa27d0-b7af-11f0-a00b-e331bd36fe07" || UPGRADE_UID="d1aa27d0-b7af-11f0-a00b-e331bd36fe06"
    $JPKGCMD --type "msi" --app-image "$IMAGE_PATH" --dest "$OUTPUT_PATH" --name "$NAME" --resource-dir "$RES/msi/${arc}" \
    --license-file "$INPUT_PATH\Licence.txt" --description "Karnak DICOM Gateway for deidentification" --win-upgrade-uuid "$UPGRADE_UID"  \
    --win-menu --win-menu-group "$NAME" --copyright "$COPYRIGHT" --app-version "$KARNAK_CLEAN_VERSION" --vendor "$VENDOR" "${tmpArgs[@]}" --verbose
    mv "$OUTPUT_PATH_UNIX/$NAME-$KARNAK_CLEAN_VERSION.msi" "$OUTPUT_PATH_UNIX/$NAME-$KARNAK_CLEAN_VERSION-${arc}.msi"
  elif [ "$machine" = "linux" ] ; then
    declare -a installerTypes=("deb" "rpm")
    for installerType in "${installerTypes[@]}"; do
      [ "${installerType}" = "rpm" ] && DEPENDENCIES="" || DEPENDENCIES="libstdc++6, libgcc1"
      $JPKGCMD --type "$installerType" --app-image "$IMAGE_PATH" --dest "$OUTPUT_PATH"  --name "$NAME" --resource-dir "$RES" \
      --license-file "$INPUT_PATH/Licence.txt" --description "Karnak DICOM Gateway for deidentification" --vendor "$VENDOR" \
      --copyright "$COPYRIGHT" --app-version "$KARNAK_CLEAN_VERSION" --linux-app-release "$REVISON_INC" \
      --linux-package-name "karnak" --linux-deb-maintainer "Nicolas Roduit" --linux-rpm-license-type "EPL-2.0" \
      --linux-menu-group "Viewer;MedicalSoftware;Graphics;" --linux-app-category "science" --linux-package-deps "${DEPENDENCIES}" \
      --linux-shortcut "${tmpArgs[@]}" --verbose
      if [ -d "${TEMP_PATH}" ] ; then
        rm -rf "${TEMP_PATH}"
      fi
    done
  elif [ "$machine" = "macosx" ] ; then
    $JPKGCMD --type "pkg" --app-image "$IMAGE_PATH.app" --dest "$OUTPUT_PATH" --name "$NAME" --resource-dir "$RES" \
    --license-file "$INPUT_PATH/Licence.txt" --copyright "$COPYRIGHT" --app-version "$KARNAK_CLEAN_VERSION" \
    "${tmpArgs[@]}" --verbose "${signArgs[@]}"
  fi
fi

"$curPath"/get-dcm4che.sh -v 5.34.1 -o "$OUTPUT_PATH/$NAME/bin/dcm4che" -a "${ARC_OS}"
chmod +x "$OUTPUT_PATH/$NAME/bin/dcm4che"/*.sh
cp -r "$curPath"/run.sh "$OUTPUT_PATH"/run.sh
chmod +x "$OUTPUT_PATH"/run.sh
