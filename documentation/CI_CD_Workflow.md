# CI/CD Workflow

**Student:** AMANKUMAR JHA · **Roll No:** 23102C0072

```mermaid
flowchart TD
    Plan[Backlog and acceptance criteria] --> Code[Implement in feature branch]
    Code --> Commit[Review and commit to Git]
    Commit --> Checkout[Jenkins checkout]
    Checkout --> Verify[Maven clean verify]
    Verify --> Selenium[Selenium end-to-end]
    Selenium --> Image[Docker build]
    Image --> Branch{main branch?}
    Branch -->|Yes| Deploy[Compose deployment with Jenkins credentials]
    Branch -->|No| Review[Review build and test reports]
    Deploy --> Health[Health endpoint and container status]
    Health --> Feedback[Demo feedback and next backlog item]
```

## Quality gates

1. **Build and JUnit:** `mvn clean verify`; failures fail the Jenkins build.
2. **Browser flow:** Selenium logs in, registers, searches, edits, and changes a membership status.
3. **Image:** Docker packages the Maven-built JAR.
4. **Deployment:** main branch only; the Compose app starts after the MySQL health check; the app exposes `/actuator/health`.

GitHub webhook delivery is not configured until the project has a remote repository and credentials. The local Jenkins pipeline can be run manually without claiming GitHub integration.

