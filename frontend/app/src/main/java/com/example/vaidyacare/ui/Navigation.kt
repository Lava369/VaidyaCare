package com.example.vaidyacare.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.net.URLDecoder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.composable

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {

        /* -------------------- WELCOME -------------------- */
        composable("welcome") {
            WelcomeScreen(
                onContinue = { navController.navigate("role") }
            )
        }

        /* -------------------- ROLE -------------------- */
        composable("role") {
            RoleSelectionScreen(navController)
        }

        /* -------------------- PATIENT LOGIN -------------------- */
        composable("loginScreen") {
            PatientLoginScreen(
                onForgotPassword = { navController.navigate("forgotPassword") },
                onSignUp = { navController.navigate("signup") },
                onLoginSuccess = { navController.navigate("home") }
            )
        }

        /* -------------------- ✅ DOCTOR -------------------- */
        composable("roleSelection") {
            RoleSelectionScreen(navController)
        }

        /* -------------------- DOCTOR LOGIN -------------------- */
        composable("doctorLogin") {
            DoctorLoginScreen(
                onBack = { navController.popBackStack() },
                onLoginSuccess = { doctorId ->
                    navController.navigate("doctorHome/$doctorId") {
                        popUpTo("doctorLogin") { inclusive = true }
                    }
                },
                onSignup = { navController.navigate("doctorSignup") },
                onForgotPassword = { navController.navigate("doctorForgotPassword") }
            )
        }

        composable(
            route = "doctorHome/{doctorId}",
            arguments = listOf(navArgument("doctorId") { type = NavType.IntType })
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getInt("doctorId") ?: 0
            DoctorHomeScreen(doctorId, navController)
        }
        /* -------------------- DOCTOR SIGNUP -------------------- */
        composable("doctorSignup") {
            DoctorSignupScreen(
                onBack = { navController.popBackStack() },
                onLoginClick = {
                    navController.navigate("doctorLogin") {
                        popUpTo("doctorSignup") { inclusive = true }
                    }
                }
            )
        }

        /* -------------------- DOCTOR HOME -------------------- */

        composable("doctorProfile/{doctorId}") { backStackEntry ->
            val doctorId = backStackEntry.arguments!!
                .getString("doctorId")!!.toInt()

            DoctorProfileScreen(
                navController = navController
            )
        }

        composable("doctorProfile") {
            DoctorProfileScreen(navController)
        }
        composable("doctorPersonalInformation") {
            DoctorPersonalInformationScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("doctorProfessionalDetails/{doctorId}") { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getInt("doctorId")
            DoctorProfessionalDetailsScreen(
                doctorId = doctorId,
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }
        composable("doctorProfessionalDetails") {
            DoctorProfessionalDetailsScreen(
                doctorId = null, // Will get from session
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }
        composable("doctorSettings") {
            DoctorSettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("doctorSync") {
            DoctorSyncScreen(
                onBack = { navController.popBackStack() }
            )
        }

        /* -------------------- DOCTOR FORGOT PASSWORD -------------------- */
        composable("doctorForgotPassword") {
            DoctorForgotPasswordScreen(
                onBack = { navController.popBackStack() },

                onSendCode = { _, _ ->
                    navController.navigate("doctorOtp")
                },

                onBackToLogin = {
                    navController.navigate("doctorLogin") {
                        popUpTo("doctorForgotPassword") { inclusive = true }
                    }
                }
            )
        }
        composable("doctorResetPassword") {
            DoctorResetPassword(
                onBack = { navController.popBackStack() },
                onPasswordReset = {
                    navController.navigate("doctorLogin") {
                        popUpTo("doctorResetPassword") { inclusive = true }
                    }
                }
            )
        }
        /* -------------------- DOCTOR OTP VERIFICATION -------------------- */
        composable("doctorOtp") {
            DoctorForgotPasswordOTP(
                onBack = { navController.popBackStack() },
                onVerify = {
                    navController.navigate("doctorResetPassword") {
                        popUpTo("doctorForgotPassword") { inclusive = false }
                    }
                }
            )
        }
        /* -------------------- TODAY'S APPOINTMENTS (DOCTOR) -------------------- */
        composable("todayAppointments") {
            TodaysAppointmentScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("pendingConsultations") {
            PendingConsultationScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("patientMessages") {
            PatientMessagesScreen(navController)
        }
        composable("medicalRecords") {
            MedicalRecordsScreen(navController)
        }
        composable("reviewRatings") {
            ReviewRatingsScreen(navController)
        }
        composable("EarningPayments") {
            EarningPaymentsScreen(navController)
        }
        composable("startConsultation") {
            StartConsultationScreen(
                onBack = { navController.popBackStack() },
                onStart = {
                    // 👉 Navigate after selecting consultation type
                    navController.navigate("consultationDetails")
                }
            )
        }
        composable("updatePatientReport") {
            UpdatePatientReportScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("updateStatus") {
            UpdateStatusScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("schedule") {
            ScheduleScreen(
                onBack = { navController.popBackStack() }
            )
        }

        /* -------------------- ADMIN -------------------- */
        composable("adminLogin") {
            AdminLoginScreen(navController = navController)
        }

        composable("adminDashboard") {
            AdminDashboard(navController)
        }

        /* -------------------- SIGNUP -------------------- */
        composable("signup") {
            SignupScreen(
                onBack = { navController.popBackStack() },
                onLoginClick = {
                    navController.navigate("loginScreen") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }
        /* -------------------- FORGOT PASSWORD -------------------- */
        composable("forgotPassword") {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },

                onSendCodeSuccess = { email ->
                    navController.navigate("otp/$email")
                },

                onBackToLogin = {
                    navController.navigate("loginScreen")
                }
            )
        }

        composable("otp") {
            VerificationCodeScreen(
                onBack = { navController.popBackStack() },
                onVerifySuccess = { navController.navigate("resetPassword") }
            )
        }

        composable("resetPassword") {
            ResetPasswordScreen(
                onBack = { navController.popBackStack() },
                onPasswordReset = { navController.navigate("loginScreen") }
            )
        }
        /* -------------------- HOME -------------------- */
        composable("home") {
            HomeScreen(navController)
        }


        /* -------------------- CONSULT NOW -------------------- */
        composable("consultNow") {
            ConsultNowScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        /* -------------------- FIND DOCTOR -------------------- */
        composable("findDoctor") {
            FindDoctorScreen(
                onBack = { navController.popBackStack() },
                onBySpecialtyClick = { navController.navigate("bySpecialty") },
                onBookAppointment = { navController.navigate("bookAppointmentFlow") }
            )
        }

        composable("bySpecialty") {
            BySpecialtyScreen(navController)
        }

        composable(
            route = "specialty_doctors/{specialization}",
            arguments = listOf(navArgument("specialization") { type = NavType.StringType })
        ) { backStackEntry ->
            val specialization = backStackEntry.arguments?.getString("specialization") ?: ""
            SpecialtyDoctorsScreen(
                navController = navController,
                specialization = java.net.URLDecoder.decode(specialization, "UTF-8")
            )
        }

        /* -------------------- SPECIALTIES -------------------- */
        composable("general_physician") {
            GeneralPhysicianScreen(
                onBack = { navController.popBackStack() },
                onBookNow = { navController.navigate("bookAppointmentFlow") }
            )
        }

        composable("cardiologist") {
            CardiologistScreen(
                onBack = { navController.popBackStack() },
                onBookNow = { navController.navigate("bookAppointmentFlow") }
            )
        }

        composable("dermatologist") {
            DermatologistScreen(
                onBack = { navController.popBackStack() },
                onBookNow = { navController.navigate("bookAppointmentFlow") }
            )
        }

        composable("pediatrician") {
            PediatricianScreen(
                onBack = { navController.popBackStack() },
                onBookNow = { navController.navigate("bookAppointmentFlow") }
            )
        }

        composable("orthopedic") {
            OrthopedicScreen(
                onBack = { navController.popBackStack() },
                onBookNow = { navController.navigate("bookAppointmentFlow") }
            )
        }

        composable("gynecologist") {
            GynecologistScreen(
                onBack = { navController.popBackStack() },
                onBookNow = { navController.navigate("bookAppointmentFlow") }
            )
        }

        composable("ent_specialist") {
            ENTSpecialistScreen(
                onBack = { navController.popBackStack() },
                onBookNow = { navController.navigate("bookAppointmentFlow") }
            )
        }

        composable("neurologist") {
            NeurologistScreen(
                onBack = { navController.popBackStack() },
                onBookNow = { navController.navigate("bookAppointmentFlow") }
            )
        }

        composable("psychiatrist") {
            PsychiatristScreen(
                onBack = { navController.popBackStack() },
                onBookNow = { navController.navigate("bookAppointmentFlow") }
            )
        }

        composable("dentist") {
            DentistScreen(
                onBack = { navController.popBackStack() },
                onBookNow = { navController.navigate("bookAppointmentFlow") }
            )
        }

        /* -------------------- BOOK APPOINTMENT -------------------- */
        composable("bookAppointmentFlow") {
            BookAppointmentFlowScreen {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = false }
                }
            }
        }

        /* -------------------- REPORTS -------------------- */
        composable("myReports") {
            MyReportsScreen(
                onBack = { navController.popBackStack() },
                onGoToHealthRecords = { navController.navigate("healthRecords") }
            )
        }

        composable("healthRecords") {
            HealthRecordsScreen(onBack = { navController.popBackStack() })
        }

        /* -------------------- HOME SERVICE -------------------- */
        composable("homeService") {
            HomeServiceScreen(navController)
        }

        composable("nurseSelection") {
            NurseSelectionScreen(navController)
        }

        composable("selectNurse/{category}") { entry ->
            val category = URLDecoder.decode(
                entry.arguments?.getString("category") ?: "",
                "UTF-8"
            )
            SelectNurseScreen(navController, category)
        }

        composable("nurseProfile/{name}") { entry ->
            val nurseName = URLDecoder.decode(
                entry.arguments?.getString("name") ?: "",
                "UTF-8"
            )
            nurseList.find { it.name == nurseName }?.let {
                NurseProfileScreen(navController, it)
            }
        }

        /* -------------------- EMERGENCY -------------------- */
        composable("emergencyAmbulance") {
            EmergencyAmbulanceScreen(navController)
        }

        composable("requestConfirmed") {
            RequestConfirmedScreen(
                onBackToHome = { navController.navigate("home") }
            )
        }

        /* -------------------- PROFILE -------------------- */
        composable("profile") {
            ProfileScreen(navController = navController)
        }

        composable("editProfile") {
            EditProfileScreen(onBack = { navController.popBackStack() })
        }

        composable("personalInfo") {
            PersonalInformationScreen(navController) {
                navController.popBackStack()
            }
        }

        composable("appointmentsHistory") {
            AppointmentsHistoryScreen(navController) {
                navController.popBackStack()
            }
        }

        composable("prescriptions") {
            PrescriptionScreen(onBack = { navController.popBackStack() })
        }

        composable("notifications") {
            NotificationScreen(onBack = { navController.popBackStack() })
        }

        composable("help_and_support") {
            HelpAndSupportScreen(navController)
        }

        composable("syncInfo") {
            SyncInformation(onBack = { navController.popBackStack() })
        }

        composable("settings") {
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        /* -------------------- FAMILY MANAGER -------------------- */
        composable("familyManager") {
            FamilyAccount()
        }
    }
}