create table if not exists orders
(
    id           integer auto_increment primary key,
    user_name    varchar(20) not null,
    product_name varchar(50) not null,
    category     varchar(10) not null,
    amount       integer     not null,
    status       varchar(10) not null,
    order_date   timestamp   not null
);

create table if not exists excel_export_jobs
(
    id              integer auto_increment primary key,
    requested_at    timestamp not null,
    started_at      timestamp,
    ended_at        timestamp,
    status          varchar(10) not null,
    file_path       varchar(200),
    index idx_requested_at (requested_at)
);
