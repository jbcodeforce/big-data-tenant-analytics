aws rds create-db-instance \
    --db-name tenantdb \
    --engine postgres \
    --db-instance-identifier mypostgresqserver \
    --allocated-storage 20 \
    --db-instance-class db.t3.micro \
    --master-username postgres \
    --master-user-password passw0rd \
    --backup-retention-period 1 \
    --vpc-security-group-ids 