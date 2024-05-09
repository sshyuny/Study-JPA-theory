package jpql;

import java.util.List;
import java.util.Collection;

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

    /*
     * JPQL 타입 표현과 기타식
     */
    static void typeExpression(EntityManager em) {
        Member member = new Member();
        member.setAge(10);
        member.setUsername("member1");
        member.setType(MemberType.ADMIN);
        em.persist(member);

        em.flush();
        em.clear();
        
        // 파라미터 바인딩 없이 jpql
        String queryWithoutParam = "select m.username, 'HELLO', true from Member m "
                      + "where m.type = jpql.MemberType.ADMIN";
        // 파라미터 바인딩 활용한 jpql
        String queryWitParam = "select m.username, 'HELLO', true from Member m "
                      + "where m.type = :userType";
                      
        List<Object[]> resultWithoutParam = em.createQuery(queryWithoutParam)
                .getResultList();
        List<Object[]> resultWithParam = em.createQuery(queryWitParam)
                .setParameter("userType", MemberType.ADMIN)
                .getResultList();

        for (Object[] objects : resultWithoutParam) {
            System.out.println("objects = " + objects[0]);
            System.out.println("objects = " + objects[1]);
            System.out.println("objects = " + objects[2]);
        }
        for (Object[] objects : resultWithParam) {
            System.out.println("objects = " + objects[0]);
            System.out.println("objects = " + objects[1]);
            System.out.println("objects = " + objects[2]);
        }
    }

    /* 
     * 조건식(CASE 등등) - case, coalesce, nullif
     */
    static void useCaseSql(EntityManager em) {
        Member member = new Member();
        member.setAge(10);
        member.setUsername("관리자");
        member.setType(MemberType.ADMIN);
        em.persist(member);

        em.flush();
        em.clear();

        String queryForCase = 
                "select " + 
                "    case when m.age <= 10 then '학생요금' " +
                "         when m.age >= 60 then '경로요금' " +
                "         else '일반요금' " +
                "end " +  
                "from Member m";
        String queryForCoalesce = "select coalesce(m.username, '이름 없는 회원') from Member m";
        String queryForNullif = "select nullif(m.username, '관리자') from Member m";

        List<String> result = em.createQuery(queryForNullif, String.class)
                .getResultList();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    /*
     * JPQL 함수
     */
    static void jpqlFunction(EntityManager em) {
        Member member = new Member();
        member.setAge(10);
        member.setUsername("관리자");
        member.setType(MemberType.ADMIN);
        em.persist(member);

        em.flush();
        em.clear();

        String queryForLocate = "select locate('de', 'abcdefg') from Member m";
        String queryForSize = "select size(t.members) from Team t";

        List<Integer> result = em.createQuery(queryForSize, Integer.class)
                .getResultList();

        for (Integer s : result) {
            System.out.println("s = " + s);
        }
    }

    /*
     * 경로 표현식
     */
    static void pathExpression(EntityManager em) {
        Team team = new Team();
        em.persist(team);

        Member member = new Member();
        member.setTeam(team);
        member.setUsername("관리자");
        em.persist(member);

        em.flush();
        em.clear();

        String queryForSomeToOne = "select m.team.name from Member m";  // 묵시적 내부조인(내부조인 발생됨!) & m.team.name 가능
        String queryForSomeToMany = "select t.members from Team t";
                // t.members는 컬렉션을 가르키기 때문에 t.members.username 불가능! t.members.size는 가능(컬렉션에 size 사용 가능하기때문) 
        String queryForSomeToManyWithAlias = "select m.username from Team t join t.members m";
                // 명시적 조인으로 얻은 별칭을 통한 탐색 가능!

        List<String> resultForSomeToOne = em.createQuery(queryForSomeToOne, String.class)
                .getResultList();
        List<Collection> resultForSomeToMany = em.createQuery(queryForSomeToMany, Collection.class)
                .getResultList();
        List<String> resultForSomeToManyWithAlias = em.createQuery(queryForSomeToManyWithAlias, String.class)
                .getResultList();
        
        System.out.println("result = " + resultForSomeToMany);
        System.out.println("result = " + resultForSomeToManyWithAlias.get(0));
    }

    /*
     * 페치 조인1 - 기본
     * 페치조인을 사용하지 않았을 때의 문제점
     */
    static void fetchJoin1WithoutFetch(EntityManager em) {
        Team teamA = new Team();
        teamA.setName("팀A");
        em.persist(teamA);
        Team teamB = new Team();
        teamB.setName("팀B");
        em.persist(teamB);
        Member member1 = new Member();
        member1.setUsername("회원1");
        member1.setTeam(teamA);
        em.persist(member1);
        Member member2 = new Member();
        member2.setUsername("회원2");
        member2.setTeam(teamA);
        em.persist(member2);
        Member member3 = new Member();
        member3.setUsername("회원3");
        member3.setTeam(teamB);
        em.persist(member3);

        em.flush();
        em.clear();

        String query = "select m from Member m ";

        List<Member> result = em.createQuery(query, Member.class)
                .getResultList();
        //이때 Member를 가져오는 쿼리가 한번 날라가고, 
        
        for (Member member : result) {
            System.out.println("Memeber = " + member.getUsername() + ", " + member.getTeam().getName());
            //지연로딩이 되어있기 때문에, member.getTeam().getName() 이 시점에 SQL 다시 날라감
            //회원1 - 팀A(SQL로 가져옴)
            //회원2 - 팀A(1차캐시에서 가져옴)
            //회원3 - 팀B(SQL로 가져옴)
        }

        //쿼리 여러번 나가게 됨!
        //만일 회원 100명의 팀이 모두 다르다면 쿼리는 101번 나가게된다.
        //이를 N + 1이라고 부름.
        //해결 방법: 패치조인! (즉시, 지연로딩 모두 해결 안됨)
    }

    /*
     * 페치 조인1 - 기본
     * 페치조인 적용
     */
    static void fetchJoin1WithFetch(EntityManager em) {
        Team teamA = new Team();
        teamA.setName("팀A");
        em.persist(teamA);
        Team teamB = new Team();
        teamB.setName("팀B");
        em.persist(teamB);
        Member member1 = new Member();
        member1.setUsername("회원1");
        member1.setTeam(teamA);
        em.persist(member1);
        Member member2 = new Member();
        member2.setUsername("회원2");
        member2.setTeam(teamA);
        em.persist(member2);
        Member member3 = new Member();
        member3.setUsername("회원3");
        member3.setTeam(teamB);
        em.persist(member3);

        em.flush();
        em.clear();

        String query = "select m from Member m join fetch m.team";

        List<Member> result = em.createQuery(query, Member.class)
                .getResultList();
        //여기서 join을 사용해서 team 데이터가 이미 담겼기 때문에
        
        for (Member member : result) {
            System.out.println("Memeber = " + member.getUsername() + ", " + member.getTeam().getName());
            // 이때 추가 쿼리 나갈 필요 없음
        }
    }

    /*
     * 페치 조인1 - 기본
     * 컬렉션 페치 조인
     */
    static void fetchJoin1Collection(EntityManager em) {
        Team teamA = new Team();
        teamA.setName("팀A");
        em.persist(teamA);
        Team teamB = new Team();
        teamB.setName("팀B");
        em.persist(teamB);
        Member member1 = new Member();
        member1.setUsername("회원1");
        member1.setTeam(teamA);
        em.persist(member1);
        Member member2 = new Member();
        member2.setUsername("회원2");
        member2.setTeam(teamA);
        em.persist(member2);
        Member member3 = new Member();
        member3.setUsername("회원3");
        member3.setTeam(teamB);
        em.persist(member3);

        em.flush();
        em.clear();

        String query = "select t from Team t join fetch t.members";

        List<Team> result = em.createQuery(query, Team.class)
                .getResultList();
        
        for (Team team : result) {
            System.out.println("team = " + team.getName() + " | members = " + team.getMembers().size());
            for (Member member : team.getMembers()) {
                System.out.println("- member =" + member);
            }
        }
    }

    /*
     * 페치 조인1 - 기본
     * 컬렉션 페치 조인 비교
     */
    static void fetchJoin1Distinct(EntityManager em) {
        Team teamA = new Team();
        teamA.setName("팀A");
        em.persist(teamA);
        Team teamB = new Team();
        teamB.setName("팀B");
        em.persist(teamB);
        Member member1 = new Member();
        member1.setUsername("회원1");
        member1.setTeam(teamA);
        em.persist(member1);
        Member member2 = new Member();
        member2.setUsername("회원2");
        member2.setTeam(teamA);
        em.persist(member2);
        Member member3 = new Member();
        member3.setUsername("회원3");
        member3.setTeam(teamB);
        em.persist(member3);

        em.flush();
        em.clear();

        String query = "select t from Team t";
        String queryWithJoin = "select t from Team t join fetch t.members";
        String queryWithDistinct = "select distinct t from Team t join fetch t.members";  //distinct 붙임!

        List<Team> result = em.createQuery(query, Team.class)
                .getResultList();
        List<Team> resultWithJoin = em.createQuery(queryWithJoin, Team.class)
                .getResultList();
        List<Team> resultDistinct = em.createQuery(queryWithDistinct, Team.class)
                .getResultList();
        
        System.out.println("result.size() = " + result.size());
        System.out.println("resultWithJoin.size() = " + resultWithJoin.size());
        System.out.println("resultDistinct.size() = " + resultDistinct.size());

        //distinct 덕분에 팀A 한번만 조회된다
        for (Team team : resultDistinct) {
            System.out.println("team = " + team.getName() + " | members = " + team.getMembers().size());
            for (Member member : team.getMembers()) {
                System.out.println("- member =" + member);
            }
        }
    }

    /*
     * 페치 조인2 - 한계
     * 위험한 상황인 일대다 페치조인 페이징
     */
    static void fetchJoin2Danger(EntityManager em) {
        Team teamA = new Team();
        teamA.setName("팀A");
        em.persist(teamA);
        Team teamB = new Team();
        teamB.setName("팀B");
        em.persist(teamB);
        Member member1 = new Member();
        member1.setUsername("회원1");
        member1.setTeam(teamA);
        em.persist(member1);
        Member member2 = new Member();
        member2.setUsername("회원2");
        member2.setTeam(teamA);
        em.persist(member2);
        Member member3 = new Member();
        member3.setUsername("회원3");
        member3.setTeam(teamB);
        em.persist(member3);

        em.flush();
        em.clear();

        // 일대다 컬렉션 페치조인에서 페이징 처리를 하기 때문에 위험하다!
        String queryDanger = "select t from Team t join fetch t.members m";
        List<Team> resultDanger = em.createQuery(queryDanger, Team.class)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();
    }

    /*
     * 페치 조인2 - 한계
     * 위험한 상황인 일대다 페치조인 페이징
     */
    static void fetchJoin2DangerAlter(EntityManager em) {
        Team teamA = new Team();
        teamA.setName("팀A");
        em.persist(teamA);
        Team teamB = new Team();
        teamB.setName("팀B");
        em.persist(teamB);
        Member member1 = new Member();
        member1.setUsername("회원1");
        member1.setTeam(teamA);
        em.persist(member1);
        Member member2 = new Member();
        member2.setUsername("회원2");
        member2.setTeam(teamA);
        em.persist(member2);
        Member member3 = new Member();
        member3.setUsername("회원3");
        member3.setTeam(teamB);
        em.persist(member3);

        em.flush();
        em.clear();


        // 대안 1: 다대일이 되도록 쿼리를 바꿔서 안전하게 조회할 수 있다.
        String querySafe1 = "select m from Member m join fetch m.team t";

        // 대안 2.
        String querySafe2 = "select t from Team t";
        List<Team> resultSafe2 = em.createQuery(querySafe2, Team.class)
                .setFirstResult(0)
                .setMaxResults(2)
                .getResultList();
        //LAZY 설정으로, 각 team마다 매번 select 쿼리 나간다는 성능 문제 있음
        for (Team team : resultSafe2) {
            System.out.println("team = " + team.getName() + " | members = " + team.getMembers().size());
            for (Member member : team.getMembers()) {
                System.out.println("- member =" + member);
            }
        }

    }

    /*
     * 네임드 쿼리
     */
    static void namedQuery1(EntityManager em) {
        Team teamA = new Team();
        teamA.setName("팀A");
        em.persist(teamA);
        Team teamB = new Team();
        teamB.setName("팀B");
        em.persist(teamB);
        Member member1 = new Member();
        member1.setUsername("회원1");
        member1.setTeam(teamA);
        em.persist(member1);
        Member member2 = new Member();
        member2.setUsername("회원2");
        member2.setTeam(teamA);
        em.persist(member2);
        Member member3 = new Member();
        member3.setUsername("회원3");
        member3.setTeam(teamB);
        em.persist(member3);

        em.flush();
        em.clear();

        List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", "회원1")
                .getResultList();

        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }

    /*
     * 벌크 연산
     */
    static void bulk(EntityManager em) {
        Team teamA = new Team();
        teamA.setName("팀A");
        em.persist(teamA);
        Team teamB = new Team();
        teamB.setName("팀B");
        em.persist(teamB);
        Member member1 = new Member();
        member1.setUsername("회원1");
        member1.setTeam(teamA);
        em.persist(member1);
        Member member2 = new Member();
        member2.setUsername("회원2");
        member2.setTeam(teamA);
        em.persist(member2);
        Member member3 = new Member();
        member3.setUsername("회원3");
        member3.setTeam(teamB);
        em.persist(member3);

        // em.flush();  플러시 자동 호출된다!
        // em.clear();

        int resultCount = em.createQuery("update Member m set m.age = 20")
                .executeUpdate();

        System.out.println("resultCount = " + resultCount);

        // 하지만 영속성 컨텍스트에는 DB내용이 반영되어있지 않기 때문에
        // 영속성 컨텍스트 초기화 없이 값을 가져올 경우 데이터 정합성이 깨진다!
        Member findMember = em.find(Member.class, member1.getId());
        System.out.println("findMember.getAge() = " + findMember.getAge());  // 업데이트 전값인 0이 나옴

        // 영속성 컨텍스트 초기화를 꼭 해주자!
        em.clear();
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
            // join1(em);
            // useCaseSql(em);
            // jpqlFunction(em);
            // pathExpression(em);

            // fetchJoin1WithoutFetch(em);
            // fetchJoin1WithFetch(em);
            // fetchJoin1Collection(em);
            // fetchJoin1Distinct(em);
            // fetchJoin2Danger(em);
            // fetchJoin2DangerAlter(em);

            // namedQuery1(em);
            bulk(em);

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