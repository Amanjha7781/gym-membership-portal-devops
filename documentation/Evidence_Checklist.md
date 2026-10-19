# Final Evidence Checklist

**Student:** AMANKUMAR JHA · **Roll No:** 23102C0072<br>
**Verification date:** 2026-10-19

The following screenshots were inspected as real PNG images. The new application screenshots were captured during the live Selenium workflow with synthetic test data; that specific test member was later removed and verified absent. Do not expose credentials, `.env` contents, or the database backup.

## Screenshots already present

- [x] Signed-out login page: `screenshots/application.png.png`.
- [x] Jenkins build #7 success summary: `screenshots/jenkins-build-7.png.png` (historical; not a pipeline stage diagram).
- [x] Jenkins aggregate test report: `screenshots/jenkins-test-results.png.png` (9 total, 8 passed, 1 skipped, 0 failed; no suite-level detail).
- [x] Jenkins build #8 stage overview: `screenshots/jenkins-build-8-stages.png` (all displayed stages passed).
- [x] Jenkins build #8 test report: `screenshots/jenkins-build-8-test-results.png` (9 total, 8 passed, 1 skipped, 0 failed).
- [x] Docker Desktop container list: `screenshots/docker-containers.png` (app, MySQL, Jenkins shown running; health separately confirmed by text evidence).
- [x] Dashboard and summary counts: `screenshots/task15-dashboard.png`.
- [x] Member list/search result after creation: `screenshots/task15-member-registration.png`.
- [x] Member update form: `screenshots/task15-member-edit-form.png`.
- [x] Admin status update reflected in filtered member list: `screenshots/task15-member-workflow.png`.

## Still needed

- [ ] Optional separate Jenkins build #8 summary screenshot; the saved stage overview shows all stages succeeded and the actual run is recorded in `jenkins-build-8-results-20261019.txt`.
- [ ] If specifically required, capture one Docker/health screenshot combining containers, image, and endpoint; current screenshot shows container state, while text evidence covers image/health.
- [x] Jenkins build #8 console and test results inspected in the authenticated browser; a concise verified record is `documentation/task15-evidence/jenkins-build-8-results-20261019.txt`. The full console export has not been saved to the repository.
- [ ] GitHub URL only if the assignment requires GitHub: no remote is configured; this requires the student's repository and authenticated account, plus approval before publishing.
- [ ] Student's final review of the local commit and submission package.

## Execution evidence

- [x] Fresh `mvn -B verify`: `documentation/task15-evidence/maven-verify-20261019-115032.txt`; 8 tests, 0 failures/errors, 1 opt-in Selenium test skipped; H2.
- [x] Fresh live Selenium/persistence run: `documentation/task15-evidence/persistence-20261019-115528.txt`; 1 test passed; created a single disposable member, verified it in MySQL before/after app-only restart, then deleted it and verified no row remained.
- [x] Current Docker health: `documentation/task15-evidence/docker-current-status-20261019.txt`; app and MySQL healthy; `/actuator/health` returned `{"status":"UP"}`; persistent MySQL volume verified.
- [x] Task 13/14 safe Ansible verification: `ansible-validation-20261019.txt`, `ansible-provision-20261019-115311.txt`, and `ansible-idempotence-20261019-115311.txt`; all syntax checks passed and both provisioning runs reported `changed=0`, `failed=0`.
- [x] Historical Task 14 controlled app-recovery and known-good rollback evidence: `deployment/ansible/task14-evidence/logs/task14-run-20261009-224252.log`.
- [x] Jenkins build #8 completed `SUCCESS`: Checkout, Build and Unit Tests, Selenium End-to-End, Docker Build, and Deploy passed. Test report: 9 total, 8 passed, 0 failed, 1 skipped; dedicated Selenium case passed. See `jenkins-build-8-results-20261019.txt`.
- [x] `.env` is ignored and is not tracked. SQL backup remains outside the project/repository.
- [x] Task 11 persistence check was app-only. MySQL was not stopped or reset; its container identity and named volume were unchanged.

See [Evidence Index](Evidence_Index.md) for the full task-by-task matrix, limitations, and submission guidance. Task 15 remains partial pending the student's final review and GitHub setup if required by the assignment. The old exact screenshot names `application.png`, `jenkins-build-7.png`, and `jenkins-test-results.png` are absent; their genuine existing legacy files are named with `.png.png` suffixes as listed above.

The full point-in-time verification record is [Task15_Final_Audit_20261019.md](task15-evidence/Task15_Final_Audit_20261019.md). The earlier [Task15_Audit_20261019.md](task15-evidence/Task15_Audit_20261019.md) documents that Docker Desktop was stopped before environment restoration.
