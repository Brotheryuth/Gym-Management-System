package com.gym.model;

import com.gym.enums.MembershipStatus;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Membership {
    private final String id;
    private MembershipPlan plan;
    private Member member;
    private Date startDate;
    private Date endDate;
    private MembershipStatus status;

    private static final DateTimeFormatter cleanDate = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    /**
     * Formats the SQL date to a clean, readable string format.
     *
     * @param formatDate The SQL Date to format
     * @return Formatted date string, or null if parameter is null
     */
    public String cleanDateFormat(Date formatDate) {
        if (formatDate != null) {
            return formatDate.toLocalDate().format(cleanDate);
        }
        return null;
    }

    /**
     * Constructor to create a new membership (ID is auto-generated).
     *
     * @param member The subscribing member
     * @param plan The membership plan
     * @param startDate The subscription start date
     */
    public Membership(Member member, MembershipPlan plan, Date startDate) {
        this.id = UUID.randomUUID().toString();
        setMember(member);
        setPlan(plan);
        setStartDate(startDate);
        setStatus(MembershipStatus.PENDING);
    }



    /**
     * Constructor to load an existing membership from the database.
     *
     * @param id The existing membership ID
     * @param member The subscribing member
     * @param plan The membership plan
     * @param startDate The subscription start date
     * @param endDate The subscription end date
     * @param status The current membership status
     */
    public Membership(String id, Member member, MembershipPlan plan, Date startDate, Date endDate, MembershipStatus status) {
        this.id = id;
        setMember(member);
        setPlan(plan);
        this.startDate = startDate;
        this.endDate = endDate;
        setStatus(status);
    }

    public String getId() {
        return id;
    }

    public MembershipPlan getPlan() {
        return plan;
    }

    public void setPlan(MembershipPlan plan) {
        if (plan == null) {
            System.out.println("Plan cannot be null.");
            return;
        }
        this.plan = plan;
        if (this.startDate != null) {
            LocalDate startLocal = this.startDate.toLocalDate();
            this.endDate = Date.valueOf(startLocal.plusMonths(plan.getDuration()));
        }
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        if (member == null) {
            System.out.println("Member cannot be null.");
            return;
        }
        this.member = member;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        if (startDate == null) {
            this.startDate = Date.valueOf(LocalDate.now());
        } else {
            this.startDate = startDate;
        }
        if (this.plan != null) {
            LocalDate startLocal = this.startDate.toLocalDate();
            this.endDate = Date.valueOf(startLocal.plusMonths(this.plan.getDuration()));
        }
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        if (endDate == null) {
            System.out.println("End date cannot be null.");
            return;
        }
        this.endDate = endDate;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    public void setStatus(MembershipStatus status) {
        if (status == null) {
            System.out.println("Invalid status. Setting default: PENDING");
            this.status = MembershipStatus.PENDING;
            return;
        }
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format(
                """
                ----------------------------------
                        MEMBERSHIP INFO
                ----------------------------------
                Membership ID   : %s
                Member ID       : %s
                Member Name     : %s
                Plan ID         : %s
                Plan Price      : $%.2f
                Start Date      : %s
                End Date        : %s
                Status          : %s
                ----------------------------------
                """,
                this.id,
                this.member != null ? this.member.getId() : "N/A",
                this.member != null ? this.member.getFullName() : "N/A",
                this.plan != null ? this.plan.getPlanID() : "N/A",
                this.plan != null ? this.plan.getPlanPrice() : 0.0,
                cleanDateFormat(this.startDate),
                cleanDateFormat(this.endDate),
                this.status
        );
    }
}
