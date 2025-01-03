package jpql;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.BatchSize;

@Entity
public class Team {
    
    @Id @GeneratedValue
    private Long id;
    private String name;

    // @BatchSize(size = 100)  // n+1 문제 해결 방법
    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Member> getMembers() {
        return members;
    }
    public void setMembers(List<Member> members) {
        this.members = members;
    }
    
}
