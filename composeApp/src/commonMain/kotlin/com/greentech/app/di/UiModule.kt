package com.greentech.app.di

import com.greentech.app.platform.CameraScanner
import com.greentech.app.platform.QrSaver
import com.greentech.app.ui.notification.NotificationManager
import com.greentech.app.ui.screens.auth.CambiarPasswordLogic
import com.greentech.app.ui.screens.auth.LoginLogic
import com.greentech.app.ui.screens.auth.SplashLogic
import com.greentech.app.ui.screens.common.AyudaLogic
import com.greentech.app.ui.screens.common.PerfilLogic
import com.greentech.app.ui.screens.employee.home.EmployeeHomeLogic
import com.greentech.app.ui.screens.employee.lotes.ListaLotesLogic
import com.greentech.app.ui.screens.employee.lotes.QrLoteLogic
import com.greentech.app.ui.screens.employee.lotes.RegistrarLoteLogic
import com.greentech.app.ui.screens.employee.monitoreo.ActividadLogic
import com.greentech.app.ui.screens.employee.monitoreo.AnomaliaLogic
import com.greentech.app.ui.screens.employee.monitoreo.BitacoraLogic
import com.greentech.app.ui.screens.employee.monitoreo.CondicionesLogic
import com.greentech.app.ui.screens.employee.monitoreo.DetalleLoteLogic
import com.greentech.app.ui.screens.employee.monitoreo.EscanerQrLogic
import com.greentech.app.ui.screens.employee.monitoreo.EtapaLogic
import com.greentech.app.ui.screens.employee.monitoreo.ObservacionLogic
import com.greentech.app.ui.screens.employee.monitoreo.RecomendacionesLogic
import com.greentech.app.ui.screens.employee.salidas.RegistrarSalidaLogic
import com.greentech.app.ui.screens.admin.home.AdminHomeLogic
import com.greentech.app.ui.screens.admin.personal.AltaTrabajadorLogic
import com.greentech.app.ui.screens.admin.personal.TrabajadoresLogic
import com.greentech.app.ui.screens.admin.reportes.DashboardLogic
import com.greentech.app.ui.screens.admin.supervision.AuditoriaLogic
import com.greentech.app.ui.screens.admin.supervision.LotesLogic
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import com.greentech.app.ui.screens.common.IntroduccionLogic
import com.russhwolf.settings.Settings
import com.greentech.app.ui.core.PreferenciasUso
import com.greentech.app.ui.screens.employee.monitoreo.RegistrosRecientes

/** Logics (ViewModels) y servicios de la UI. */
val uiModule = module {
    single { NotificationManager() }
    single { CameraScanner() }
    single { QrSaver() }
    single { PreferenciasUso(Settings()) }
    single { RegistrosRecientes() }

    // Comunes
    viewModelOf(::SplashLogic)
    viewModelOf(::LoginLogic)
    viewModelOf(::CambiarPasswordLogic)
    viewModelOf(::PerfilLogic)
    viewModelOf(::AyudaLogic)
    viewModelOf(::IntroduccionLogic)

    // Empleado (los que reciben loteId lo toman de parametersOf en el grafo)
    viewModelOf(::EmployeeHomeLogic)
    viewModel { ListaLotesLogic(get()) }
    viewModelOf(::RegistrarLoteLogic)
    viewModel { p -> QrLoteLogic(p.get(), p.get(), get(), get()) }
    viewModelOf(::EscanerQrLogic)
    viewModel { p -> DetalleLoteLogic(p.get(), p.get(), get(), get(), get()) }
    viewModel { p -> EtapaLogic(p.get(), get(), get(), get(), get()) }
    viewModel { p -> ObservacionLogic(p.get(), get(), get(), get(), get()) }
    viewModel { p -> ActividadLogic(p.get(), get(), get(), get(), get()) }
    viewModel { p -> CondicionesLogic(p.get(), get(), get()) }
    viewModel { p -> AnomaliaLogic(p.get(), get(), get(), get(), get()) }
    viewModel { p -> RecomendacionesLogic(p.get(), get(), get()) }
    viewModel { p -> BitacoraLogic(p.get(), p.get(), get(), get()) }
    viewModel { p -> RegistrarSalidaLogic(p.get(), get(), get(), get(), get()) }

    // Administrador
    viewModelOf(::AdminHomeLogic)
    viewModelOf(::DashboardLogic)
    viewModelOf(::TrabajadoresLogic)
    viewModelOf(::AltaTrabajadorLogic)
    viewModel { LotesLogic(get()) }
    viewModelOf(::AuditoriaLogic)
}
