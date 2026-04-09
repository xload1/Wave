alter table user_account
    drop constraint uk_user_account_username;

alter table user_account
    rename column username to dispay_name;