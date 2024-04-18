package hellojpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Hibernate;

public class JpaMain {

    static void practiceJpql(EntityManager em) {
        List<Member> result = em.createQuery(
            "select m from Member m where m.username like '%kim%' ",
            Member.class
        ).getResultList();

        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    static void practiceCriteria(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Member> query = cb.createQuery(Member.class);

        Root<Member> m = query.from(Member.class);

        CriteriaQuery<Member> cq = query.select(m).where(cb.equal(m.get("username"), "kim"));
        List<Member> resultList = em.createQuery(cq)
                .getResultList();
    }
    
    public static void main(String args[]) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            // practiceJpql(em);
            practiceCriteria(em);

            //  [등록]
            // Member member = new Member();
            // member.setId(2L);
            // member.setName("helloB");
            // em.persist(member);

            //  [수정]
            // Member findMember = em.find(Member.class, 1L);
            // findMember.setName("helloJPA");

            //  [JPQL]
            // List<Member> result = em.createQuery("select m from Member as m", Member.class)
            //         .getResultList();
            // for (Member member : result) {
            //     System.out.println("member.name = " + member.getName());
            // }

            //  [비영속]
            // Member member = new Member();
            // member.setId(101L);
            // member.setName("HelloJPA");
            
            //  [영속]
            // System.out.println("=== BEFORE ===");
            // em.persist(member);
            // System.out.println("=== AFTER ===");

            // (이때 1차 캐시에서 조회하기때문에 select 쿼리 나가지 않음!)
            // Member findMember = em.find(Member.class, 101L);

            // System.out.println("findmember.id = " + findMember.getId());
            // System.out.println("findmember.name = " + findMember.getName());

            //  [저장된 객체 DB와 1차캐시에서 조회] [동일한 Member]
            // Member findMember1 = em.find(Member.class, 101L);  //DB에서 조회
            // Member findMember2 = em.find(Member.class, 101L);  //1차캐시에서 조회
            // System.out.println("result = " + (findMember1 == findMember2));

            //  [쓰기지연]
            // Member member1 =  new Member(150L, "A");
            // Member member2 =  new Member(160L, "B");
            // em.persist(member1);
            // em.persist(member2);
            // System.out.println("=== 쓰기 지연으로 persist 이후에 쿼리가 나가기 때문에 이 이후 select 쿼리 발생!");

            // [변경감지]
            // Member member = em.find(Member.class, 150L);
            // member.setName("ZZZ");

            //  [플러시]
            // Member member = new Member(200L, "member200");
            // em.persist(member);
            // em.flush();
            // System.out.println("=== flush() 호출되면 바로 DB에 반영되기 때문에, 이 print 이전에 쿼리 발생!");

            //  [준영속]
            // Member member = em.find(Member.class, 150L);
            // member.setName("AAA");
            // em.detach(member);
            // em.clear();
            // Member member2 = em.find(Member.class, 150L);
            //영속성 컨텍스트를 초기화한 다음 조회한 것이기 때문에, member2를 조회할 때도 select 쿼리 나감

            // Member member = new Member();
            // member.setUsername("C");
            // em.persist(member);

            //[연관관계매핑 - 단방향연관관계]
            // Team team = new Team();
            // team.setName("TeamA");
            // em.persist(team);
            
            // Member member = new Member();
            // member.setUsername("member1");
            // // member.changeTeam(team); // 연관관계 편의 메서드는 양 쪽 다 있으면 좋지 않음
            // em.persist(member);
            
            // members는 mappedby로 설정되어 읽기 전용이라, 이 내용 DB에 반영되지 않음!
            // 그러나 같이 매핑해두는 것이 객체지향적이고 실수를 막기에 좋기 때문에 권장됨.
            // 두 곳을 항상 누락하지 않고 매핑하는 것은 쉽지 않음.
            // 연관관계 편의 메서드를 통해 해결 가능!
            // 여기서 연관관계 편의 메서드는 예시로 Member.setTeam()에서 아래 매핑 메서드를 같이 넣어두는 것 가능.
            // team.getMembers().add(member);

            // em.flush();
            // em.clear();

            // Team findTeam = em.find(Team.class, team.getId());
            // List<Member> members = findTeam.getMembers();
            // //양방향매핑시, 아래처럼 toString()에서 스택오버플로우 발생됨(toString에 필드들 값을 반환하는 내용이 있을경우)
            // System.out.println("members = " + findTeam);

            // Member findMember = em.find(Member.class, member.getId());
            // Team findTeam = findMember.getTeam();
            // List<Member> members = findMember.getTeam().getMembers();
            // for (Member m : members) {
            //     System.out.println("m = " + m.getUsername());
            // }

            // Member member = new Member();
            // member.setUsername("member1");
            // em.persist(member);

            // Team team =  new Team();
            // team.setName("teamA");
            // team.getMembers().add(member);
            // em.persist(team);

            // [상속관계]
            // 상속관계에서 저장하면 어떻게 되는지 각 설정별로 확인
            // Movie movie = new Movie();
            // movie.setDirector("aaa");
            // movie.setActor("bbb");
            // movie.setName("바람과함께사라지다");
            // movie.setPrice(10000);
            // em.persist(movie);

            // 상속관계에서, 자신으로 조회
            // Movie findMovie = em.find(Movie.class, movie.getId());
            // System.out.println("findMovie = " + findMovie);
            // 상속관계에서, 부모로 조회
            // Item item = em.find(Item.class, movie.getId());
            // System.out.println("item = " + item);

            // Member member = new Member();
            // member.setUsername("user1");
            // member.setCreatedBy("kim");
            // member.setCreatedDate(LocalDateTime.now());
            // em.persist(member);

            // em.flush();
            // em.clear();

            // [프록시]x
            // Member member = new Member();
            // member.setUsername("hello");
            
            // em.persist(member);

            // em.flush();
            // em.clear();

            // Member findMember = em.find(Member.class, member.getId());
            // System.out.println(findMember.getId());
            // System.out.println(findMember.getUsername());

            // 1. 프록시 기본
            // Member member = new Member();
            // member.setUsername("hello");
            // em.persist(member);
            
            // em.flush();
            // em.clear();

            // Member findMember = em.getReference(Member.class, member.getId());
            // System.out.println("findMember = " + findMember.getClass());
            // System.out.println("findMember.id = " + findMember.getId());
            // // // 아래 print 부분으로 인해 getReference값이 실제로 사용되면, 그 시점에 select 쿼리 나감
            // System.out.println("findMember.username = " + findMember.getUsername());
            // System.out.println("findMember.username = " + findMember.getUsername());

            // 2. 프록시 타입 비교
            // Member member1 = new Member();
            // member1.setUsername("member1");
            // em.persist(member1);

            // Member member2 = new Member();
            // member2.setUsername("member2");
            // em.persist(member2);

            // em.flush();
            // em.clear();

            // Member m1 = em.find(Member.class, member1.getId());
            // // 아래 코드로 인해 이미 엔티티 올라가있으면, getReference()에서 프록시객체 사용하지 않음
            // Member m2 = em.find(Member.class, member2.getId());
            // Member m2Refer = em.getReference(Member.class, member2.getId());

            // System.out.println("m1 == m2 : " + (m1.getClass() == m2.getClass()));
            // System.out.println("m1 == m2Refer : " + (m1.getClass() == m2Refer.getClass()));  // m2가 주석처리되어있다면  false
            // System.out.println("객체 == 객체 : " + (m2 == m2Refer));

            // 2-2. 프록시타입 비교
            // Member refMember = em.getReference(Member.class, member1.getId());
            // System.out.println("refMember = " + refMember.getClass());  // Proxy 반환

            // Member findMember = em.find(Member.class, member1.getId());
            // System.out.println("findMember = " + findMember.getClass());  // Member 클래스 반환?

            // System.out.println("refMember == findMember: " + (refMember == findMember));  // 얘는?

            // 2-3. 
            // Member member1 = new Member();
            // member1.setUsername("member1");
            // em.persist(member1);

            // em.flush();
            // em.clear();

            // Member refMember = em.getReference(Member.class, member1.getId());
            // System.out.println("refMember = " + refMember.getClass());

            // 준영속 상태로 만들고
            // em.detach(refMember);
            // em.close();
            // em.clear();
            // 프록시 초기화하면 에러발생함!
            // refMember.getUsername();

            // System.out.println("isLoaded = " + emf.getPersistenceUnitUtil().isLoaded(refMember));
            // //강제초기화
            // Hibernate.initialize(refMember);
            // //초기화 확인
            // System.out.println("isLoaded = " + emf.getPersistenceUnitUtil().isLoaded(refMember));

            // [지연로딩, 즉시로딩]
            // Team team = new Team();
            // team.setName("teamA");
            // em.persist(team);

            // Member member1 = new Member();
            // member1.setUsername("member1");
            // member1.setTeam(team);

            // em.persist(member1);

            // em.flush();
            // em.clear();

            //지연로딩일 경우, team에 프록시 객체가 반환되고
            //즉시로딩일 경우, team에 진짜 객체가 반환됨
            // Member m = em.find(Member.class, member1.getId());
            // System.out.println("m = " + m.getTeam().getClass());  // 프록시 반환? 엔티티 반환?

            // System.out.println("==========");
            // m.getTeam().getName();  // 이 시점에 Team 조회! = 초기화
            // System.out.println("==========");

            // System.out.println("m = " + m.getTeam().getClass());
            
            // [즉시 로딩 N+1 문제]
            // Team teamA = new Team();
            // teamA.setName("teamA");
            // em.persist(teamA);
            // Team teamB = new Team();
            // teamB.setName("teamB");
            // em.persist(teamB);

            // Member member1 = new Member();
            // member1.setUsername("member1");
            // member1.setTeam(teamA);
            // em.persist(member1);
            // Member member2 = new Member();
            // member2.setUsername("member2");
            // member2.setTeam(teamB);
            // em.persist(member2);
            // Member member3 = new Member();
            // member3.setUsername("member2");
            // member3.setTeam(teamB);
            // em.persist(member3);

            // em.flush();
            // em.clear();

            //N + 1 문제
            //team의 데이터 개수만큼 조회쿼리 나감
            // List<Member> members = em.createQuery("select m from Member m", Member.class)
            //         .getResultList();


            // [영속성 전이]
            // Child child1 = new Child();
            // Child child2 = new Child();

            // Parent parent = new Parent();
            // parent.addChild(child1);
            // parent.addChild(child2);

            // em.persist(parent);
            // // 아래처럼 매번 child에도 persist를 넣기 귀찮을 수 있음. 그때 cascade 사용하면 됨!
            // em.persist(child1);
            // em.persist(child2);
            
            // //떨어뜨리기
            // em.flush();
            // em.clear();

            // Parent findParent = em.find(Parent.class, parent.getId());
            // // findParent.getChildList().remove(0);  // 고아객체 데이터도 삭제됨
            // em.remove(findParent);

            //[값타입]
            // Address address = new Address("city", "street", "1000");

            // Member member1 = new Member();
            // member1.setUsername("member1");
            // member1.setHomeAddress(address);
            // member1.setWorkPeriod(new Period());
            // em.persist(member1);

            // //값객체를 새로 만들어서 통으로 바꿈!
            // Address newAddress = new Address("NewCity", address.getStreet(), address.getZipCode());
            // member1.setHomeAddress(newAddress);

            // Member member2 = new Member();
            // member2.setUsername("member2");
            // member2.setHomeAddress(copyAddress);
            // em.persist(member2);

            // //복사한 값이기 때문에, 영향을 받지 않음
            // member1.getHomeAddress().setCity("newCity");

            // [값타입 컬렉션]
            // Member member = new Member();
            // member.setUsername("member1");
            // member.setHomeAddress(new Address("homneCity", "street", "10000"));

            // member.getFavoritFoods().add("치킨");
            // member.getFavoritFoods().add("족발");
            // member.getFavoritFoods().add("피자");

            // member.getAddressHistory().add(new AddressEntity("old1", "street", "10000"));
            // member.getAddressHistory().add(new AddressEntity("old2", "street", "10000"));

            // em.persist(member);

            // em.flush();
            // em.clear();

            // Member findMember = em.find(Member.class, member.getId());

            // 값타입은 immutable해야 하기 때문에, 아래처럼 setter로 수정하면 안됨!
            // findMember.getHomeAddress().setCity("newCity");

            // 아예 새로운 값타입을 생성해서 (통으로) 넣어주어야 함!
            // System.out.println("=============================================");
            // Address a = findMember.getHomeAddress();
            // findMember.setHomeAddress(new Address("newCity", a.getStreet(), a.getZipCode()));

            //치킨 -> 한식 수정
            // findMember.getFavoritFoods().remove("치킨");
            // findMember.getFavoritFoods().add("한식");

            // findMember.getAddressHistory().remove(new Address("old1", "street", "10000"));
            // findMember.getAddressHistory().add(new Address("newCity1", "street", "10000"));
    
            tx.commit();
            
        } catch (Exception e) {
           tx.rollback(); 
           e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }

    /**
     * Team 정보 없이 Member만 가져오고 싶은 상황!
     * team 같이 조회하지 않아도 됨
     */
    private static void printMember(Member member) {
        System.out.println("member = " + member);
    }
    /**
     * Member 조회하며 team도 같이 땡겨와야 유리한 상황
     */
    // private static void printMemberAndTeam(Member member) {
    //     String username = member.getUsername();
    //     System.out.println("username = " + username);

    //     Team team = member.getTeam();
    //     System.out.println("team = " + team);

    // }
}
