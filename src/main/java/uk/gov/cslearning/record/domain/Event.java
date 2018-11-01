package uk.gov.cslearning.record.domain;

import javax.persistence.*;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String catalogueId;

    @Column(nullable = false)
    private String path;

    public Event() {}

    public Event(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCatalogueId() {
        return catalogueId;
    }

    public void setCatalogueId(String catalogueId) {
        this.catalogueId = catalogueId;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", catalogueId='" + catalogueId + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
