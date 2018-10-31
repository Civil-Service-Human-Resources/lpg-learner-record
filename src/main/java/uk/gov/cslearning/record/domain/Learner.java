package uk.gov.cslearning.record.domain;

import javax.persistence.*;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;

@Entity
public class Learner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String uuid;

    public Learner() {}

    public Learner(int id){
        checkArgument(id >= 0);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
