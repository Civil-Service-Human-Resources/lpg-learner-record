package uk.gov.cslearning.record.domain;

import javax.persistence.*;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;

@Entity
public class Learner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String uuid;

    public Learner(){}

    public Learner(Long id){
        checkArgument(id >= 0);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "Learner{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}
