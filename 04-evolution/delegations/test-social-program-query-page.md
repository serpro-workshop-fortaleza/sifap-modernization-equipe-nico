# Delegation: Route-level tests for the social program query page

## Status

| Field | Value |
|---|---|
| Draft | `04-evolution/issues/test-social-program-query-page.md` |
| Technical review | Passed against REQ-011 and REQ-012 on 2026-09-11 |
| GitHub Issue | Pending publication |
| Assigned to Copilot | No |
| Started at | Not started |
| Pull request | Not available |
| Current result | Draft approved; delegation pending manual publication |

## Publication

Review the draft with the team before publishing it. Confirm that the labels
exist in the repository, then run:

```bash
gh issue create \
  --title "Add route-level tests for the social program query page" \
  --body-file 04-evolution/issues/test-social-program-query-page.md \
  --label "enhancement"
```

After publication, assign the Issue to Copilot through the GitHub interface and
replace the pending fields above with the Issue URL, start time, and assignment
status.

The repository currently provides the `enhancement` label, but does not provide
the suggested `test`, `frontend`, or `copilot-agent` labels. The REST assignee
check also did not expose `copilot` as assignable, so availability must be
confirmed in the GitHub interface after publication.

## Expected Change

- Expected size: small, fewer than 100 changed lines.
- Expected new file:
  `frontend/tests/pagina-de-consulta.test.tsx`.
- Production files should remain unchanged unless a test exposes an existing
  defect that is demonstrated in the pull request.
- Expected requirements: REQ-011 and REQ-012.

## Tracking Checklist

- [ ] The team approved the draft and its acceptance criteria.
- [ ] The Issue was published and its URL was recorded.
- [ ] The Issue was assigned to Copilot.
- [ ] Copilot opened a pull request targeting `develop`.
- [ ] The pull request changes only the agreed scope.
- [ ] `npm run lint` passes from `frontend/`.
- [ ] `npm run typecheck` passes from `frontend/`.
- [ ] `npm run test:coverage` passes from `frontend/`.
- [ ] A human reviewer checked the pull request.
- [ ] The review result or next action was recorded.

## Review Guide

Check the generated pull request for:

- a real route-level test rather than duplicated component-only assertions;
- a module-boundary mock of `consultarPrograma` without network access;
- separate coverage of the found and not-found results;
- inline REQ-011 and REQ-012 references;
- no fabricated APIs, imports, requirements, or labels;
- no production dependency, backend, schema, authentication, or UI changes;
- no empty assertions, snapshots without intent, or disabled tests;
- target branch `develop` and a green CI run.

## Responsibility

This is delegation, not automation. The team is responsible for review, the
integration decision, and its consequences. The Copilot Agent contributes but
does not approve.

## Result

The technical review passed: the acceptance criteria are limited to the
existing REQ-011 and REQ-012 behavior, the expected change fits in one small
pull request, and no requirement or architecture decision was added.

Delegation has not started because the Issue has not been published. No pull
request or Agent output exists to review. After manual publication, record the
Issue URL, assignment result, pull request URL, files changed, tests added, CI
result, review decision, and any manual corrections.
