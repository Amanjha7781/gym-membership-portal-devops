# Deliberately unhealthy test candidate; never use as an application image.
# It remains alive so Docker reports an unhealthy state and the rollback test
# can exercise the recovery workflow without exiting/restart-loop behavior.
FROM gym-portal-ansible:local
ENTRYPOINT ["/bin/sh", "-c", "sleep 3600"]
