FROM medicalinformatics/mainzelliste:1.8-latest

COPY mainzelliste-entrypoint.sh /mainzelliste-entrypoint.sh
COPY mainzelliste.conf /mainzelliste.conf.default

ENTRYPOINT ["/mainzelliste-entrypoint.sh"]