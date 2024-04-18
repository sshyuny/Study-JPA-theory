package hellojpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
public class Product {
    
    @Id @GeneratedValue
    private Long id;

    private String name;

    //실무에서 사용하지 않는 다대다매핑 방법
    // @ManyToMany(mappedBy = "products")
    // private List<Member> members = new ArrayList<>();

    //다대다매핑 대신 연결테이블 사용
    @OneToMany(mappedBy = "product")
    private List<MemberProduct> memberProducts = new ArrayList<>();

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

    
}
