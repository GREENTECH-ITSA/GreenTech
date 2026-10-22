package com.greentech.shared.di

import com.greentech.shared.data.fake.FakeAuthRepository
import com.greentech.shared.data.fake.FakeLoteRepository
import com.greentech.shared.data.fake.FakeMonitoreoRepository
import com.greentech.shared.data.fake.FakeReporteRepository
import com.greentech.shared.data.fake.FakeSalidaRepository
import com.greentech.shared.data.fake.FakeStore
import com.greentech.shared.data.fake.FakeUsuarioRepository
import com.greentech.shared.domain.repository.AuthRepository
import com.greentech.shared.domain.repository.LoteRepository
import com.greentech.shared.domain.repository.MonitoreoRepository
import com.greentech.shared.domain.repository.ReporteRepository
import com.greentech.shared.domain.repository.SalidaRepository
import com.greentech.shared.domain.repository.UsuarioRepository
import com.greentech.shared.domain.usecase.ActualizarEtapaUseCase
import com.greentech.shared.domain.usecase.AltaTrabajadorUseCase
import com.greentech.shared.domain.usecase.BajaTrabajadorUseCase
import com.greentech.shared.domain.usecase.BuscarLotePorCodigoUseCase
import com.greentech.shared.domain.usecase.CambiarPasswordUseCase
import com.greentech.shared.domain.usecase.CerrarSesionUseCase
import com.greentech.shared.domain.usecase.LoginUseCase
import com.greentech.shared.domain.usecase.ObtenerActividadRecienteUseCase
import com.greentech.shared.domain.usecase.ObtenerAlertasUseCase
import com.greentech.shared.domain.usecase.ObtenerAuditoriaUseCase
import com.greentech.shared.domain.usecase.ObtenerBitacoraUseCase
import com.greentech.shared.domain.usecase.ObtenerCatalogosLoteUseCase
import com.greentech.shared.domain.usecase.ObtenerCondicionesUseCase
import com.greentech.shared.domain.usecase.ObtenerDashboardUseCase
import com.greentech.shared.domain.usecase.ObtenerLoteUseCase
import com.greentech.shared.domain.usecase.ObtenerLotesUseCase
import com.greentech.shared.domain.usecase.ObtenerRecomendacionesUseCase
import com.greentech.shared.domain.usecase.ObtenerSesionUseCase
import com.greentech.shared.domain.usecase.ObtenerTrabajadoresUseCase
import com.greentech.shared.domain.usecase.RegistrarActividadUseCase
import com.greentech.shared.domain.usecase.RegistrarAnomaliaUseCase
import com.greentech.shared.domain.usecase.RegistrarLoteUseCase
import com.greentech.shared.domain.usecase.RegistrarObservacionUseCase
import com.greentech.shared.domain.usecase.RegistrarSalidaUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * true  → repositorios Fake en memoria (estado actual del proyecto).
 * false → repositorios reales de Supabase (data/repository, pendientes de implementar).
 */
const val USE_FAKE_DATA = true

private val fakeRepositoriesModule = module {
    single { FakeStore() }
    single<AuthRepository> { FakeAuthRepository(get()) }
    single<LoteRepository> { FakeLoteRepository(get()) }
    single<MonitoreoRepository> { FakeMonitoreoRepository(get()) }
    single<SalidaRepository> { FakeSalidaRepository(get()) }
    single<UsuarioRepository> { FakeUsuarioRepository(get()) }
    single<ReporteRepository> { FakeReporteRepository(get()) }
}

private val supabaseRepositoriesModule = module {
    // TODO(Supabase): registrar aquí SupabaseProvider y las implementaciones reales, por ejemplo:
    // single<AuthRepository> { SupabaseAuthRepository(get()) }
}

private val useCaseModule = module {
    factory { LoginUseCase(get()) }
    factory { CambiarPasswordUseCase(get()) }
    factory { ObtenerSesionUseCase(get()) }
    factory { CerrarSesionUseCase(get()) }
    factory { ObtenerLotesUseCase(get()) }
    factory { ObtenerLoteUseCase(get()) }
    factory { BuscarLotePorCodigoUseCase(get()) }
    factory { ObtenerCatalogosLoteUseCase(get()) }
    factory { RegistrarLoteUseCase(get()) }
    factory { ActualizarEtapaUseCase(get()) }
    factory { RegistrarObservacionUseCase(get()) }
    factory { RegistrarActividadUseCase(get()) }
    factory { RegistrarAnomaliaUseCase(get()) }
    factory { ObtenerCondicionesUseCase(get()) }
    factory { ObtenerAlertasUseCase(get()) }
    factory { ObtenerRecomendacionesUseCase(get()) }
    factory { ObtenerBitacoraUseCase(get()) }
    factory { ObtenerActividadRecienteUseCase(get()) }
    factory { RegistrarSalidaUseCase(get(), get()) }
    factory { ObtenerTrabajadoresUseCase(get()) }
    factory { AltaTrabajadorUseCase(get()) }
    factory { BajaTrabajadorUseCase(get(), get()) }
    factory { ObtenerDashboardUseCase(get()) }
    factory { ObtenerAuditoriaUseCase(get()) }
}

/** Módulos de Koin del módulo shared (datos + casos de uso). */
val sharedModules: List<Module> = listOf(
    if (USE_FAKE_DATA) fakeRepositoriesModule else supabaseRepositoriesModule,
    useCaseModule,
)
