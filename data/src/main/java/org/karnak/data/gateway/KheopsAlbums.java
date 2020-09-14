package org.karnak.data.gateway;

import javax.persistence.*;

@Entity(name = "KheopsAlbums")
@Table(name = "kheops_albums")
public class KheopsAlbums {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String urlAPI;
    private String authorizationDestination;
    private String authorizationSource;
    private String condition;

    @ManyToOne
    private Destination destination = new Destination();

    public KheopsAlbums() {}

    public KheopsAlbums(String urlAPI, String authorizationDestination,
                        String authorizationSource, String condition) {
        this.urlAPI = urlAPI;
        this.authorizationDestination = authorizationDestination;
        this.authorizationSource = authorizationSource;
        this.condition = condition;
    }

    public Long getId() {
        return id;
    }

    public String getUrlAPI() {
        return urlAPI;
    }

    public void setUrlAPI(String urlAPI) {
        this.urlAPI = urlAPI;
    }

    public String getAuthorizationDestination() {
        return authorizationDestination;
    }

    public void setAuthorizationDestination(String authorizationDestination) {
        this.authorizationDestination = authorizationDestination;
    }

    public String getAuthorizationSource() {
        return authorizationSource;
    }

    public void setAuthorizationSource(String authorizationSource) {
        this.authorizationSource = authorizationSource;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
