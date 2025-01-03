package hellojpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Parent {
    
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    // @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    // @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Child> childList = new ArrayList<>();

    /**
     * 연관관계 편의 메서드
     */
    public void addChild(Child child) {
        childList.add(child);
        child.setParent(this);
    }

    
    
    /* 게터 세터 */

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
    public List<Child> getChildList() {
        return childList;
    }
    public void setChildList(List<Child> childList) {
        this.childList = childList;
    }
    
}
