# supabase/

La base de datos de GreenTech **ya existe en Supabase (nube)**. Esta carpeta queda vacía a propósito.

Aquí se podrán guardar más adelante, si se decide versionarlos:

- `migrations/` — scripts SQL exportados con `supabase db dump` o `supabase migration new`.
- `functions/` — Edge Functions (por ejemplo, alta y baja de usuarios de Auth desde el panel del administrador).
- `seed.sql` — datos de ejemplo equivalentes a `shared/.../data/fake/FakeData.kt`.

La app todavía **no se conecta** a Supabase: usa repositorios falsos en memoria
(`USE_FAKE_DATA = true` en `shared/src/commonMain/kotlin/com/greentech/shared/di/SharedModule.kt`).
