package hellojpa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
// @SequenceGenerator(name = "member_seq_generator", sequenceName = "member_seq")
// @TableGenerator(
//     name = "MEMBER_SEQ_GENERATOR", 
//     table = "MY_SEQUENCES",
//     pkColumnValue = "MEMBER_SEQ", 
//     allocationSize = 1
// )
public class Member {
    
    @Id
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_generator")
    // @GeneratedValue(strategy = GenerationType.TABLE, 
    //         generator = "MEMBER_SEQ_GENERATOR")
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    // @Column(name = "name", nullable = false)
    @Column(name = "USERNAME")
    private String username;

    //기간 Period
    @Embedded
    private Period workPeriod;

    //주소
    @Embedded
    // @AttributeOverrides({
    //     @AttributeOverride(name = "city", column = @Column(name = "WORK_CITY")),
    //     @AttributeOverride(name = "street", column = @Column(name = "WORK_STREET")),
    //     @AttributeOverride(name = "zipCode", column = @Column(name = "WORK_ZIPCODE")),
    // })
    private Address homeAddress;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "city", column = @Column(name = "WORK_CITY")),
        @AttributeOverride(name = "street", column = @Column(name = "WORK_STREET")),
        @AttributeOverride(name = "zipCode", column = @Column(name = "WORK_ZIPCODE")),
    })
    private Address workAddress;

    @ElementCollection
    @CollectionTable(name = "FAVORITE_FOOD", joinColumns = 
        @JoinColumn(name = "MEMBER_ID")
    )
    @Column(name = "FOOD_NAME") // 값이 하나만 들어가기 때문에, 예외적으로 column명 지정 가능함
    private Set<String> favoritFoods = new HashSet<>();

    //값타입을 컬렉션으로 저장 -> 값타입 컬렉션 더이상 사용하지 않음!
    // @ElementCollection
    // @CollectionTable(name = "ADDRESS", joinColumns = 
    //     @JoinColumn(name = "MEMBER_ID")
    // )
    // private List<Address> addressHistory = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "MEMBER_ID")
    private List<AddressEntity> addressHistory = new ArrayList<>();

    //주소
    // @Embedded
    // private Address workAddress;

    @ManyToOne(fetch = FetchType.EAGER)
    // @JoinColumn(name = "TEAM_ID", insertable = false, updatable = false)
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    // private LocalDateTime startDate;
    // private LocalDateTime endDate;
    // private String city;
    // private String street;
    // private String zipCode;

    // @OneToOne
    // @JoinColumn(name = "LOCKER_ID")
    // private Locker locker;

    //다대다매핑-실무에서 쓰지않음!
    // @ManyToMany
    // @JoinTable(name = "MEMBER_PRODUCT")
    // private List<Product> products = new ArrayList<>();

    //다대다매핑 대신 연결테이블 사용
    // @OneToMany(mappedBy = "member")
    // private List<MemberProduct> memberProducts = new ArrayList<>();

    // @ManyToOne
    // @JoinColumn(name = "TEAM_ID")
    // private Team team;

    // @Column(name = "TEAM_ID")
    // private Long teamId;

    // private Integer age;

    // @Enumerated(EnumType.STRING)
    // private RoleType roleType;

    // @Temporal(TemporalType.TIMESTAMP)
    // private Date createdDate;

    // @Temporal(TemporalType.TIMESTAMP)
    // private Date lastModifiedDate;

    // @Lob
    // private String description;

    public Member() {
    }
    
    // @Override
    // public String toString() {
    //     return "Member [id=" + id + ", username=" + username + ", team=" + team + "]";
    // }

    /**
     * 연관관계 편의 메서드!
     * (상대쪽 연관관계 편의 메서드와 이 메서드 중 하나만 선택해서 사용)
     */
    // public void changeTeam(Team team) {
    //     this.team = team;
    //     team.getMembers().add(this);
    // }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Period getWorkPeriod() {
        return workPeriod;
    }

    public void setWorkPeriod(Period workPeriod) {
        this.workPeriod = workPeriod;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Set<String> getFavoritFoods() {
        return favoritFoods;
    }

    public void setFavoritFoods(Set<String> favoritFoods) {
        this.favoritFoods = favoritFoods;
    }

    public List<AddressEntity> getAddressHistory() {
        return addressHistory;
    }

    public void setAddressHistory(List<AddressEntity> addressHistory) {
        this.addressHistory = addressHistory;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    
}
