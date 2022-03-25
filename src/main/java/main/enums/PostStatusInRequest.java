package main.enums;

public enum PostStatusInRequest {
    pending (PostStatusInDB.NEW),
    declined (PostStatusInDB.DECLINED),
    published (PostStatusInDB.ACCEPTED);

    private final PostStatusInDB statusInDB;

    PostStatusInRequest(PostStatusInDB statusInDB) {
        this.statusInDB = statusInDB;
    }

    public String getStatusInDB() {
        return statusInDB.toString();
    }
}
