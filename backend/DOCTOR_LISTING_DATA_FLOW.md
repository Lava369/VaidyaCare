# Doctor Listing Data Flow

## Overview
This document explains how doctor professional details flow from `DoctorProfessionalDetails.kt` to the listing screens.

## Data Flow

1. **Doctor Updates Professional Details** (`DoctorProfessionalDetails.kt`)
   - Saves to: `update_doctor_professional_details.php`
   - Fields saved:
     - `full_name` → Database
     - `primary_specialization` → Database
     - `experience_years` → Database
     - `consultation_fee` → Database
     - `doctor_mode` → Database

2. **Listing Screens Fetch Data**
   - `ConsultNow.kt` → `get_all_doctors.php`
   - `FindDoctor.kt` → `get_all_doctors.php`
   - `BySpecialty.kt` → `SpecialtyDoctorsScreen.kt` → `get_doctors_by_specialization.php`

3. **Backend APIs Return Data**
   - `get_all_doctors.php` maps:
     - `full_name` / `display_name` → `name`
     - `primary_specialization` → `specialization`
     - `experience_years` → `experience`
     - `consultation_fee` → `fee`
     - `doctor_mode` → `status`

4. **Android Screens Display**
   - `doctor.name` → Full Name
   - `doctor.specialization` → Primary Specialization
   - `doctor.experience` → Experience Years
   - `doctor.fee` → Consultation Fee
   - `doctor.status` → Doctor Mode (Online/Busy/Offline)

## Testing

1. Update professional details in `DoctorProfessionalDetails.kt`
2. Check database: `SELECT full_name, primary_specialization, experience_years, consultation_fee, doctor_mode FROM doctors WHERE doctor_id = ?`
3. Test API: `GET http://10.26.77.190/vaidyacare/api/get_all_doctors.php`
4. Check listing screens: `ConsultNow.kt`, `FindDoctor.kt`, `BySpecialty.kt`

## Troubleshooting

If data is not displaying:
1. Verify database columns exist (run `add_doctor_professional_fields.sql`)
2. Check API response in Postman
3. Verify doctor status is 'approved'
4. Check Android logs for errors

