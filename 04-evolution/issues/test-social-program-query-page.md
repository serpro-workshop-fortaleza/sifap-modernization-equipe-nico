# Issue: Add route-level tests for the social program query page

## Context

The frontend already tests the API client and the presentation components for
the social program query flow. However,
`frontend/app/programas-sociais/[codigo]/page.tsx` has no direct test, so the
route orchestration between `params`, `consultarPrograma`, and the rendered
state is not covered.

This issue adds focused regression tests for the existing behavior specified by
REQ-011 and REQ-012. It does not introduce new behavior.

## Acceptance Criteria

- [ ] Given an existing social program, when the query page receives its code,
      then it calls `consultarPrograma` with that code and presents exactly the
      six fields required by REQ-011.
- [ ] Given an unknown social program code, when the query page receives that
      code, then it presents the not-found message required by REQ-012.
- [ ] The new tests include inline references to REQ-011 and REQ-012.
- [ ] `npm run lint`, `npm run typecheck`, and `npm run test:coverage` pass from
      `frontend/`.
- [ ] Frontend line coverage remains at or above 60%.

## Files Probably Affected

Create:

- `frontend/tests/pagina-de-consulta.test.tsx`

Consult without changing unless a test exposes a defect:

- `frontend/app/programas-sociais/[codigo]/page.tsx`
- `frontend/app/programas-sociais/[codigo]/detalhe.tsx`
- `frontend/lib/api/programas-sociais.ts`
- `frontend/tests/detalhe-do-programa.test.tsx`
- `frontend/tests/programas-sociais-api.test.ts`
- `specs/001-social-program-catalog/spec.md`

## Testing Approach

- Mock `consultarPrograma` at the module boundary.
- Invoke the asynchronous page component with a resolved `params` promise.
- Render the returned element with Testing Library.
- Assert the existing-program and not-found paths separately.
- Keep assertions focused on route orchestration; do not duplicate all API
  client or presentation-component tests.

## Out of Scope

- Changes to backend endpoints, database schema, or Flyway migrations.
- Changes to the page layout, labels, or styling.
- Authentication, authorization, or replacement of the provisional
  `SIFAPSYS` audit user.
- New production dependencies.
- Changes to requirements or acceptance criteria in the specification.

## Labels

- `test`
- `copilot-agent`
- `frontend`

## Related Requirements

- REQ-011: Query a social program by code and present exactly the six specified
  fields.
- REQ-012: Inform the user when the requested social program is not found and
  distinguish that outcome from a processing error.
