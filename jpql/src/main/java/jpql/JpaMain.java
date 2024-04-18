package jpql;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class JpaMain {

    /*
     * TypedQuery와 Query 사용 방법
     */
    static void basicGrammar1(EntityManager em) {
        Member member = new Member();
        member.setUsername("member1");
        member.setAge(10);
        em.persist(member);

        TypedQuery<Member> query1 = em.createQuery("select m from Member m", Member.class);
        TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);
        Query query3 = em.createQuery("select m.username, m.age from Member m");

        List<Member> resultList = query1.getResultList();
    }

    /*
     * TypedQuery에서 getSingleResult()를 사용했는데 아무것도 반환되지 않으면
     * javax.persistence.NoResultException: No entity found for query 예외 발생됨!
     */
    static void basicGrammar2(EntityManager em) {
        TypedQuery<Member> query = em.createQuery("select m from Member m", Member.class);
        Member singleResult = query.getSingleResult();
    }

    /*
     * TypedQuery 파라미터 바인딩
     */
    static void basicGrammar3(EntityManager em) {
        Member member = new Member();
        member.setUsername("member1");
        member.setAge(10);
        em.persist(member);

        // TypedQuery<Member> query = em.createQuery("select m from Member m where m.username = :username", Member.class);
        // query.setParameter("username", "member1");
        // Member singleResult = query.getSingleResult();

        Member result = em.createQuery("select m from Member m where m.username = :username", Member.class)
            .setParameter("username", "member1")
            .getSingleResult();

        System.out.println("result = " + result.getUsername());
    }

    /*
     * 프로젝션 - 엔티티 프로젝션
     * 엔티티 프로젝션에서 조회된 엔티티는 영속성 컨텍스트에서 관리된다.
     */
    static void projection1(EntityManager em) {
        Member member = new Member();
        member.setUsername("member1");
        member.setAge(10);
        em.persist(member);

        em.flush();
        em.clear();

        List<Member> result = em.createQuery("select m from Member m", Member.class)
        .getResultList();
        
        Member findMember = result.get(0);
        // 영속성 컨텍스트에서 관리된다면, findMember에서 set한 내용이 반영될 것이다!
        findMember.setAge(20);
    }

    /*
     * 프로젝션 - 엔티티 프로젝션
     * 엔티티 프로젝션에서 엔티티의 필드를 조회할 경우, 조인절 실행된다.
     */
    static void projection2(EntityManager em) {
        Member member = new Member();
        member.setUsername("member1");
        member.setAge(10);
        em.persist(member);

        em.flush();
        em.clear();

        // jpql에서는 아래처럼 단일 select를 사용한것과 다르게
        // 실제 나간 쿼리는 inner join 사용됨!
        // 하지만 조인은 조심히 써야하기 때문에 "select t from Member m join m.team t" 이렇게 조인을 보여주는 것이 권장된다.
        List<Team> result = em.createQuery("select m.team from Member m", Team.class)
                .getResultList();
    }

    /*
     * 프로젝션 - 임베디드 프로젝션
     */
    static void projection3(EntityManager em) {
        Member member = new Member();
        member.setUsername("member1");
        member.setAge(10);
        em.persist(member);

        em.flush();
        em.clear();

        em.createQuery("select o.address from Order o", Address.class)
                .getResultList();
    }

    /*
     * 프로젝션 - 스칼라 프로젝션
     */
    static void projection4(EntityManager em) {
        Member member = new Member();
        member.setUsername("member1");
        member.setAge(10);
        em.persist(member);

        em.flush();
        em.clear();

        em.createQuery("select distinct m.username, m.age from Member m")
                .getResultList();
    }

    /*
     * 프로젝션 - 여러 값 조회
     * Object
     */
    static void projection5(EntityManager em) {
        Member member = new Member();
        member.setUsername("member1");
        member.setAge(10);
        em.persist(member);

        em.flush();
        em.clear();

        // 1. Query 타입으로 조회
        List results1 = em.createQuery("select distinct m.username, m.age from Member m")
                .getResultList();

        Object o = results1.get(0);
        Object[] result1 = (Object[]) o;  // 이렇게 casting해서 사용 가능!

        System.out.println("username = " + result1[0]);
        System.out.println("age = " + result1[1]);

        // 2. Object[] 타입으로 조회
        // TypedQuery 사용해서, 이렇게 한번에 casting 과정 넣어버릴수도 있다.
        List<Object[]> results2 = em.createQuery("select distinct m.username, m.age from Member m")
                .getResultList();
        Object[] result2 = results2.get(0);
        System.out.println("username = " + result2[0]);     
        System.out.println("age = " + result2[1]);

        // 3. new
        List<MemberDTO> results3 = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                .getResultList();
        MemberDTO memberDTO = results3.get(0);
        System.out.println("memberDTO = " + memberDTO.getUsername());
        System.out.println("memberDTO = " + memberDTO.getAge());
    }

    static void paging1(EntityManager em) {

        for (int i = 0; i < 100; i++) {
            Member member = new Member();
            member.setUsername("member" + i);
            member.setAge(i);
            em.persist(member);
        }

        em.flush();
        em.clear();

        // order by 넣는 이유: sorting이 되면서 페이징도 되며 잘 가져오는지 확인해보기 위해  
        List<Member> results = em.createQuery("select m from Member m order by m.age desc", Member.class)
                .setFirstResult(1)  //0이 아니라 1 넣은 이유: 0 주면 H2 쿼리에서 limit만 나가고 offset 나가지 않음
                .setMaxResults(10)
                .getResultList();
        
        System.out.println("results.size = " + results.size());
        for (Member member : results) {
            System.out.println(member);
        }
    }

    /*
     * 조인 - 내부 조인, 외부조인
     */
    static void join1(EntityManager em) {
        Team team = new Team();
        team.setName("teamA");
        em.persist(team);

        Member member = new Member();
        member.setAge(10);
        member.setUsername("member1");
        member.setTeam(team);
        em.persist(member);

        em.flush();
        em.clear();

        String queryInnerJoin = "select m from Member m inner join m.team t";  // 이렇게 하면 쿼리에서 Team t를 쓸 수 있다!
        String queryOuterJoin = "select m from Member m left outer join m.team t";
        String querySetaJoin = "select m from Member m, Team t where m.username = t.name";

        String queryJoinOnForFiltering = "select m from Member m left join m.team t on t.name = 'teamA'";
        String queryJoinOnForWihtoutRelation = "select m from Member m left join Team t on m.username = t.name";
                // 연관관계가 없는 채 조인하는 것이기 때문에 "left join Team t" 이렇게 따로 선언함!
                // 위 쿼리에서 "left join m.team t"인 것과 다름!

        List<Member> results = em.createQuery(queryJoinOnForWihtoutRelation, Member.class)
                .getResultList();
    }

    
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            // basicGrammar1(em);
            // basicGrammar2(em);
            // basicGrammar3(em);

            // projection1(em);
            // projection2(em);
            // projection3(em);
            // projection4(em);
            // projection5(em);

            // paging1(em);

            join1(em);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }
}