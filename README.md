# Real Estate Management System
## Overview
This Android application allows users to manage their prescribed drugs.
Users can add, edit, delete, and view detailed information about drugs, including scheduling, doctor info, and daily tracking (whether the drug was received today).
The app automatically updates drug status (active/inactive) and resets daily flags using WorkManager.
## Features
✔ Drug Management
- Add new prescription drugs
- Edit existing drugs
- Delete drugs
- View all drugs in a structured list
✔ Drug Details
- Name & description
- Start and end date
- Time term (before breakfast, at lunch, etc.)
- Doctor name & location
- Active status (auto-updated based on dates)
- Last date received
- “Received Today” flag
✔ Automatic Background Updates
Using WorkManager:
- Drugs become active automatically when their start date arrives
- Drugs become inactive automatically after their end date
- “Received Today” resets every new day
✔ Map Integration
- Option to view the doctor's location on Google Maps (Requires Maps installed on the device)
## Tech Stack
- Programming Language: Java
- Platform: Android SDK
- Architecture: Activities, Adapters, SQLite (Room)
- Database: Room Persistence Library
- Background Tasks: WorkManager
- UI: XML layouts
## Installation & Setup
- Clone the Repository
```sh
git clone https://github.com/vasilisbask/Prescription-Drug-Manager.git
```