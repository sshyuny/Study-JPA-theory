package hellojpa;

import java.time.LocalDateTime;

import javax.persistence.Embeddable;

@Embeddable
public class Period {
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    /**
     * 이런 객체지향적 메서드 사용 가능!
     */
    public boolean isWork() {
        return false;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    public LocalDateTime getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

}
