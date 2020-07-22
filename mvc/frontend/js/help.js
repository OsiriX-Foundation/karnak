import {PolymerElement,html} from '@polymer/polymer/polymer-element.js';
import '@polymer/paper-input/paper-input.js';

class HelpView extends PolymerElement {

    static get template() {
        return html`
                <h1 id="profile">Profile</h1>
                <p>In KARNAK on the profile page, you can upload your custom profile.</p>
                <p>A profile in KARNAK is a list of different profile or action defined on a scope of tags. During the de-identification, KARNAK will built this list and apply to a tag and its value each profile in the list. If a profile apply his action on a tag, KARNAK will move on to the next tag. So if the first profile used the tag, the next profile will never be called on that tag.</p>
                <p>Currently the profile must be a yaml file (MIME-TYPE: <strong>application/x-yaml</strong>) and respect the definition as below.</p>
                <h2 id="metadata">Metadata</h2>
                <p>The &quot;headers&quot; of the profile are this metadata. All this headers are optional, but for a better user experience we recommend that set the name to a minimum.</p>
                <p><code>name</code> - The name of your profile.</p>
                <p><code>version</code> - The version of your profile.</p>
                <p><code>minimumkarnakversion</code> - The version of KARNAK when the profile has been built.</p>
                <p><code>defaultIssuerOfPatientID</code> - this value is used to built the patient&#39;s pseudonym.</p>
                <p><code>profiles</code> - The list of profile applied on the desidentification. The first profile of this list is first called.</p>
                <h2 id="profile">Profile</h2>
                <p>A profile is defined as below in the yaml.</p>
                <p><code>name</code> - The name of your profile</p>
                <p><code>codename</code> - The profile has been applied (Must exist in KARNAK)). The list of possible profiles is defined below.</p>
                <p><code>action</code> - The action to apply to the profile. For example K (keep), X (remove)</p>
                <p><code>tags</code> - List of tags for the current profile. The action defined in the profile will be applied on this list of tags.</p>
                <p><code>exceptedtags</code> - This list represents the tags where the action will not be applied. This means that if a tag defined in this list appears for this profile, it will give control to the next profile.</p>
                <h3 id="tag">Tag</h3>
                <p>The tags can be defined on different format: <code>(0010,0010)</code>; <code>0010,0010</code>; <code>00100010</code>;</p>
                <p>A tag pattern can be defined as below: <code>(0010,XXXX)</code> groups all these tags <code>(0010, [0000-FFFF])</code></p>
                <h3 id="codename">codename</h3>
                <p><code>basic.dicom.profile</code> is the <a href="http://dicom.nema.org/medical/dicom/current/output/chtml/part15/chapter_E.html">basic profile defined by DICOM</a>. This profile applied the action defined by DICOM on the tags that identifies.</p>
                <p><strong>We strongly recommend to use this profile systematically</strong></p>
                <p>This profile need this parameters:</p>
                <ul>
                <li>name</li>
                <li>codename</li>
                </ul>
                <p>Example:</p>
                <pre><code>- <span class="hljs-built_in">name</span>: <span class="hljs-string">"DICOM basic profile"</span>
                  codename: <span class="hljs-string">"basic.dicom.profile"</span>
                </code></pre><p><code>action.on.specific.tags</code> is a profile that apply a action on a group of tags defined by the user. The action possible is:</p>
                <ul>
                <li>K - keep</li>
                <li>X - remove</li>
                </ul>
                <p>This profile need this parameters:</p>
                <ul>
                <li>name</li>
                <li>codename</li>
                <li>action</li>
                <li>tags</li>
                </ul>
                <p>This profile can have these optional parameters:</p>
                <ul>
                <li>exceptedtags</li>
                </ul>
                <p>In this example, all tags starting with 0028 will be removed excepted (0028,1199) which will be kept.</p>
                <pre><code>- name: <span class="hljs-string">"Remove tags"</span>
                <span class="hljs-symbol">  codename:</span> <span class="hljs-string">"action.on.specific.tags"</span>
                <span class="hljs-symbol">  action:</span> <span class="hljs-string">"X"</span>
                <span class="hljs-symbol">  tags:</span>
                    - <span class="hljs-string">"(0028,xxxx)"</span>
                <span class="hljs-symbol">  exceptedtags:</span>
                    - <span class="hljs-string">"(0028,1199)"</span>
                
                - name: <span class="hljs-string">"Keep tags 0028,1199"</span>
                <span class="hljs-symbol">  codename:</span> <span class="hljs-string">"action.on.specific.tags"</span>
                <span class="hljs-symbol">  action:</span> <span class="hljs-string">"K"</span>
                <span class="hljs-symbol">  tags:</span>
                    - <span class="hljs-string">"0028,1199"</span>
                </code></pre><p><code>action.on.privatetags</code> is a profile that apply a action given on all private tags or a group of private tags defined by the user. If the tag given isn&#39;t private, the profile will not be called. The action possible is:</p>
                <ul>
                <li>K - keep</li>
                <li>X - remove</li>
                </ul>
                <p>This profile need this parameters:</p>
                <ul>
                <li>name</li>
                <li>codename</li>
                <li>action</li>
                </ul>
                <p>This profile can have these optional parameters:</p>
                <ul>
                <li>tags</li>
                <li>exceptedtags</li>
                </ul>
                <p>In this example, all tags starting with 0009 will be kept and all private tags will be deleted.</p>
                <pre><code>- name: <span class="hljs-string">"Keep private tags starint with 0009"</span>
                <span class="hljs-symbol">  codename:</span> <span class="hljs-string">"action.on.privatetags"</span>
                <span class="hljs-symbol">  action:</span> <span class="hljs-string">"K"</span>
                <span class="hljs-symbol">  tags:</span>
                    - <span class="hljs-string">"(0009,xxxx)"</span>
                
                - name: <span class="hljs-string">"Remove all private tags"</span>
                <span class="hljs-symbol">  codename:</span> <span class="hljs-string">"action.on.privatetags"</span>
                <span class="hljs-symbol">  action:</span> <span class="hljs-string">"X"</span>
                </code></pre><h2 id="a-full-example-of-profile">A full example of profile</h2>
                <p>This example remove two tags not defined in the basic DICOM profile, keep the Philips PET private group and apply the basic DICOM profile.</p>
                <p>The tag 0008,0008 is the Image identification characteristics and the tag 0008,0013 is the Instance Creation Time.</p>
                <p>The tag pattern (0073,xx00) and (7053,xx09) is defined as <a href="http://dicom.nema.org/medical/dicom/current/output/chtml/part15/sect_E.3.10.html">Philips PET Private Group by DICOM</a>.</p>
                <pre><code><span class="hljs-attribute">name</span>: <span class="hljs-string">"An Example"</span>
                <span class="hljs-attribute">version</span>: <span class="hljs-string">"1.0"</span>
                <span class="hljs-attribute">minimumkarnakversion</span>: <span class="hljs-string">"0.1"</span>
                <span class="hljs-attribute">defaultIssuerOfPatientID</span>: <span class="hljs-string">"DPA"</span>
                <span class="hljs-attribute">profiles</span>:
                  - <span class="hljs-attribute">name</span>: <span class="hljs-string">"Remove tags 0008,0008; 0008,0013"</span>
                    <span class="hljs-attribute">codename</span>: <span class="hljs-string">"action.on.specific.tags"</span>
                    <span class="hljs-attribute">action</span>: <span class="hljs-string">"X"</span>
                    <span class="hljs-attribute">tags</span>:
                      - <span class="hljs-string">"0008,0008"</span>
                      - <span class="hljs-string">"0008,0013"</span>
                
                  - <span class="hljs-attribute">name</span>: <span class="hljs-string">"Keep Philips PET Private Group"</span>
                    <span class="hljs-attribute">codename</span>: <span class="hljs-string">"action.on.privatetags"</span>
                    <span class="hljs-attribute">action</span>: <span class="hljs-string">"K"</span>
                    <span class="hljs-attribute">tags</span>:
                      - <span class="hljs-string">"(7053,xx00)"</span>
                      - <span class="hljs-string">"(7053,xx09)"</span>
                
                  - <span class="hljs-attribute">name</span>: <span class="hljs-string">"DICOM basic profile"</span>
                    <span class="hljs-attribute">codename</span>: <span class="hljs-string">"basic.dicom.profile"</span>
                </code></pre>
        `;
    }

    static get is() {
        return 'help-view';
    }
}

customElements.define(HelpView.is, HelpView);