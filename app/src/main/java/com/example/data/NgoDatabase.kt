package com.example.data

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// ==========================================
// 1. DATABASE ENTITIES (Schema)
// ==========================================

@Entity(tableName = "donations")
data class Donation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val donorName: String,
    val amount: Double,
    val category: String,
    val date: String,
    val paymentMethod: String,
    val memo: String = ""
)

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val budget: Double,
    val currentSpent: Double,
    val progressPercent: Int,
    val location: String,
    val beneficiaryCount: Int,
    val teamMembers: String, // Comma separated
    val startDate: String,
    val endDate: String,
    val impactStory: String = ""
)

@Entity(tableName = "volunteers")
data class Volunteer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val email: String,
    val phone: String,
    val skills: String, // Comma separated
    val availability: String,
    val hoursTracked: Int = 0,
    val isApproved: Boolean = false,
    val refCode: String = ""
)

@Entity(tableName = "vacancies")
data class Vacancy(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val type: String, // "Vacancy" or "Volunteer Opening" or "Internship" or "Fellowship"
    val category: String, // "Education" or "Technology" or "Social Support" or "Women Empowerment"
    val description: String,
    val deadline: String,
    val appliedCount: Int = 0
)

@Entity(tableName = "scholarships")
data class Scholarship(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val applicantName: String,
    val email: String,
    val targetProgram: String,
    val incomeBracket: String,
    val currentSchool: String,
    val whyNeeded: String,
    val documentUploaded: String = "No File Set",
    val status: String = "Pending", // "Pending", "Approved", "Rejected"
    val dateApplied: String
)

@Entity(tableName = "forum_posts")
data class ForumPost(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val authorName: String,
    val title: String,
    val content: String,
    val likes: Int = 0,
    val category: String, // "General" or "Discussion" or "Youth Ideas" or "Feedback"
    val timestamp: String
)

@Entity(tableName = "financials")
data class FinancialRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "Income" or "Expense"
    val title: String,
    val projectCategory: String, // Educational Reform, Disaster response, etc.
    val amount: Double,
    val date: String
)

// ==========================================
// 2. DATA ACCESS OBJECT (DAO)
// ==========================================

@Dao
interface NgoDao {
    // Donations
    @Query("SELECT * FROM donations ORDER BY id DESC")
    fun getAllDonations(): Flow<List<Donation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonation(donation: Donation)

    // Projects
    @Query("SELECT * FROM projects ORDER BY id DESC")
    fun getAllProjects(): Flow<List<Project>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project)

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: Int): Project?

    // Volunteers
    @Query("SELECT * FROM volunteers ORDER BY id DESC")
    fun getAllVolunteers(): Flow<List<Volunteer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVolunteer(volunteer: Volunteer)

    @Query("UPDATE volunteers SET isApproved = :approved, hoursTracked = :hours WHERE id = :id")
    suspend fun updateVolunteerStatus(id: Int, approved: Boolean, hours: Int)

    // Vacancies
    @Query("SELECT * FROM vacancies ORDER BY id DESC")
    fun getAllVacancies(): Flow<List<Vacancy>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVacancy(vacancy: Vacancy)

    @Query("UPDATE vacancies SET appliedCount = appliedCount + 1 WHERE id = :id")
    suspend fun incrementVacancyApplications(id: Int)

    // Scholarships
    @Query("SELECT * FROM scholarships ORDER BY id DESC")
    fun getAllScholarships(): Flow<List<Scholarship>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScholarship(scholarship: Scholarship)

    @Query("UPDATE scholarships SET status = :status WHERE id = :id")
    suspend fun updateScholarshipStatus(id: Int, status: String)

    // Forum Posts
    @Query("SELECT * FROM forum_posts ORDER BY id DESC")
    fun getAllForumPosts(): Flow<List<ForumPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForumPost(post: ForumPost)

    @Query("UPDATE forum_posts SET likes = likes + 1 WHERE id = :id")
    suspend fun triggerPostLike(id: Int)

    // Financials
    @Query("SELECT * FROM financials ORDER BY id DESC")
    fun getAllFinancials(): Flow<List<FinancialRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinancialRecord(record: FinancialRecord)
}

// ==========================================
// 3. ROOM DATABASE CLASS
// ==========================================

@Database(
    entities = [
        Donation::class,
        Project::class,
        Volunteer::class,
        Vacancy::class,
        Scholarship::class,
        ForumPost::class,
        FinancialRecord::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NgoDatabase : RoomDatabase() {
    abstract fun ngoDao(): NgoDao

    companion object {
        @Volatile
        private var INSTANCE: NgoDatabase? = null

        fun getDatabase(context: android.content.Context, scope: CoroutineScope): NgoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NgoDatabase::class.java,
                    "future_minds_ngo_db"
                )
                .addCallback(NgoDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class NgoDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.ngoDao())
                }
            }
        }

        suspend fun populateInitialData(dao: NgoDao) {
            // Seed Projects
            dao.insertProject(
                Project(
                    title = "Rural Education Empowerment",
                    description = "Providing modern digital textbooks, stationery, and hybrid schooling to students in remote schools across Solukhumbu and Humla districts.",
                    budget = 500000.0,
                    currentSpent = 380000.0,
                    progressPercent = 76,
                    location = "Solukhumbu & Humla, Nepal",
                    beneficiaryCount = 1200,
                    teamMembers = "Sujan Pokhrel, Anita Shrestha, Rohan Thapa",
                    startDate = "2026-01-15",
                    endDate = "2026-08-30",
                    impactStory = "Passang Sherpa, a grade 6 student of Solukhumbu now reads computer science using tablets gifted through Future Minds, boosting his digital vision!"
                )
            )
            dao.insertProject(
                Project(
                    title = "Digital Literacy for Young Girls",
                    description = "A comprehensive 3-month coding, office tools, and Internet safety bootcamp for underprivileged high-school girls in Kathmandu valley.",
                    budget = 350000.0,
                    currentSpent = 245000.0,
                    progressPercent = 70,
                    location = "Kathmandu & Lalitpur, Nepal",
                    beneficiaryCount = 450,
                    teamMembers = "Deepika Sen, Samir Karki",
                    startDate = "2026-02-10",
                    endDate = "2026-06-15",
                    impactStory = "Reena Maharjan from Patan started developing basic HTML websites, breaking gender stereotype barriers in tech!"
                )
            )
            dao.insertProject(
                Project(
                    title = "Women Skills Development Caravan",
                    description = "Mobile tailoring, organic farming, and micro-entrepreneurship training to uplift marginalized females in rural Terai districts.",
                    budget = 400000.0,
                    currentSpent = 360000.0,
                    progressPercent = 90,
                    location = "Sarlahi & Mahottari, Nepal",
                    beneficiaryCount = 800,
                    teamMembers = "Renu Chaudhary, Prabhat Adhikari",
                    startDate = "2025-11-01",
                    endDate = "2026-05-30",
                    impactStory = "Over 120 certified women established self-run tailoring kiosks in Sarlahi, earning sustainable family incomes."
                )
            )
            dao.insertProject(
                Project(
                    title = "Terai Disaster Relief Program",
                    description = "Pre-monsoon flood preparedness, early warning installations, and emergency distribution of nutrition and hygiene packs.",
                    budget = 600000.0,
                    currentSpent = 300000.0,
                    progressPercent = 50,
                    location = "Rautahat, Nepal",
                    beneficiaryCount = 2000,
                    teamMembers = "Sujan Pokhrel, Dipesh Bhatta",
                    startDate = "2026-04-01",
                    endDate = "2026-09-30",
                    impactStory = "Early warning SMS structures developed under this campaign have kept over 400 household safe from rapid rise in river waters."
                )
            )

            // Seed Transparency - Incomes
            dao.insertFinancialRecord(FinancialRecord(type = "Income", title = "Global Youth Grant - EU", projectCategory = "Grant Funding", amount = 850000.0, date = "2026-01-20"))
            dao.insertFinancialRecord(FinancialRecord(type = "Income", title = "Corporate Sponsorship - Nepal Telecom", projectCategory = "Education & Tech", amount = 450000.0, date = "2026-02-15"))
            dao.insertFinancialRecord(FinancialRecord(type = "Income", title = "Public Crowdfunding Support", projectCategory = "Individual Donors", amount = 280000.0, date = "2026-03-05"))
            dao.insertFinancialRecord(FinancialRecord(type = "Income", title = "Nepali Diaspora Foundation (US)", projectCategory = "Disaster Fund", amount = 600000.0, date = "2026-04-12"))

            // Seed Transparency - Expenses
            dao.insertFinancialRecord(FinancialRecord(type = "Expense", title = "Bought 30 Hybrid Android Tablets", projectCategory = "Rural Education Empowerment", amount = 220000.0, date = "2026-02-01"))
            dao.insertFinancialRecord(FinancialRecord(type = "Expense", title = "Classroom Setup & Mobile Router Kit", projectCategory = "Digital Literacy for Young Girls", amount = 950000.0, date = "2026-03-10"))
            dao.insertFinancialRecord(FinancialRecord(type = "Expense", title = "Seed capital for 3 Women Cooperatives", projectCategory = "Women Skills Development", amount = 150000.0, date = "2026-04-05"))
            dao.insertFinancialRecord(FinancialRecord(type = "Expense", title = "Fencing and Siren Sensors for Floods", projectCategory = "Terai Disaster Relief Program", amount = 110000.0, date = "2026-05-02"))

            // Seed Vacancies
            dao.insertVacancy(
                Vacancy(
                    title = "Rural Education Field Lead",
                    type = "Vacancy",
                    category = "Education",
                    description = "Coordinate and deploy hybrid digital tablets across schools in Solukhumbu. Requires intermediate educational leadership skills and fitness to travel to high altitudes.",
                    deadline = "2026-06-30"
                )
            )
            dao.insertVacancy(
                Vacancy(
                    title = "Voluntary Coding Instructor",
                    type = "Volunteer Opening",
                    category = "Technology",
                    description = "Teach young girls HTML, CSS, & basic JavaScript during our weekend computer bootcamps. Support and mentorship from Nepalese tech group guaranteed.",
                    deadline = "2026-06-18"
                )
            )
            dao.insertVacancy(
                Vacancy(
                    title = "Social Research Internship",
                    type = "Internship",
                    category = "Social Support",
                    description = "Assess Terai disaster relief survey metrics and draft community risk logs. Guided by experienced research advisers.",
                    deadline = "2026-07-10"
                )
            )

            // Seed Forum Posts
            dao.insertForumPost(
                ForumPost(
                    authorName = "Aayush Bhattarai",
                    title = "How can we address high dropouts in Humla schools?",
                    content = "Many children drop out during harvesting seasons. Future Minds should align class timelines with local farm schedules to keep learning going!",
                    likes = 12,
                    category = "Youth Ideas",
                    timestamp = "2 hours ago"
                )
            )
            dao.insertForumPost(
                ForumPost(
                    authorName = "Shradha Karki",
                    title = "Volunteering with Terai Disaster project was magical",
                    content = "It was intense but incredibly fulfilling. The community's gratitude when receiving sirens is something I will cherish forever.",
                    likes = 24,
                    category = "Discussion",
                    timestamp = "1 day ago"
                )
            )

            // Seed initial sample donations
            dao.insertDonation(Donation(donorName = "Prashant K.C.", amount = 15000.0, category = "Education Support", date = "2026-05-24", paymentMethod = "eSewa", memo = "Sponsoring children study kits"))
            dao.insertDonation(Donation(donorName = "Namrata Shah", amount = 10000.0, category = "Disaster Relief", date = "2026-05-25", paymentMethod = "Khalti", memo = "Relief flood packs"))
            dao.insertDonation(Donation(donorName = "Samir Adhikari", amount = 5000.0, category = "Youth Programs", date = "2026-05-26", paymentMethod = "Fonepay", memo = "Encouraging digital skills"))
            dao.insertDonation(Donation(donorName = "Dr. Shubh Nepalese", amount = 25000.0, category = "Health Support", date = "2026-05-26", paymentMethod = "Bank Transfer", memo = "Terai healthcare camp support"))

            // Seed initial approved sample volunteers (so their cards show up!)
            dao.insertVolunteer(
                Volunteer(
                    fullName = "Sujan Pokhrel",
                    email = "pokhrelsujan215@gmail.com",
                    phone = "9841234567",
                    skills = "Kotlin, Community Mobilization, Public Speaking",
                    availability = "Weekends (Sat - Sun)",
                    hoursTracked = 45,
                    isApproved = true,
                    refCode = "FMN-5098"
                )
            )
        }
    }
}
