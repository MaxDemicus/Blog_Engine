package main.enums;

public enum ModerationDecision {
    accept (PostStatusInDB.ACCEPTED),
    decline (PostStatusInDB.DECLINED);

    private final PostStatusInDB status;

    ModerationDecision(PostStatusInDB status) {
        this.status = status;
    }

    public PostStatusInDB getStatus() {
        return status;
    }
}
