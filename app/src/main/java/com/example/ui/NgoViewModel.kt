package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Simple chat message model local to UI
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class NgoViewModel(
    application: Application,
    private val repository: NgoRepository
) : AndroidViewModel(application) {

    // ------------------------------------------
    // RECTIVE RAW STREAMS FROM ROOM DATABASE
    // ------------------------------------------
    val donations: StateFlow<List<Donation>> = repository.allDonations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projects: StateFlow<List<Project>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val volunteers: StateFlow<List<Volunteer>> = repository.allVolunteers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vacancies: StateFlow<List<Vacancy>> = repository.allVacancies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scholarships: StateFlow<List<Scholarship>> = repository.allScholarships
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val forumPosts: StateFlow<List<ForumPost>> = repository.allForumPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val financials: StateFlow<List<FinancialRecord>> = repository.allFinancials
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(4000), emptyList())

    // ------------------------------------------
    // ACTIVE WORKFLOW STATE FLOWS
    // ------------------------------------------
    private val _currentUserRole = MutableStateFlow("Member") // "Member", "Donor", "Volunteer", "Admin"
    val currentUserRole: StateFlow<String> = _currentUserRole.asStateFlow()

    private val _currentUserName = MutableStateFlow("Bikram Nepal")
    val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

    private val _currentUserEmail = MutableStateFlow("bikram.nepal@example.com")
    val currentUserEmail: StateFlow<String> = _currentUserEmail.asStateFlow()

    // Tracking the user's active applications so they see them under status trackers
    private val _userVolunteerId = MutableStateFlow<Int?>(null)
    val userVolunteerId: StateFlow<Int?> = _userVolunteerId.asStateFlow()

    private val _userScholarshipId = MutableStateFlow<Int?>(null)
    val userScholarshipId: StateFlow<Int?> = _userScholarshipId.asStateFlow()

    // Chat states
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("Namaste sathi! 🙏 I am Sagar, your Future Minds AI guide. Chat with me regarding our active rural projects, volunteering roles, transparent accounting ledgers or applying for education grants!", false)
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // Selected items for modal/detail sheets
    private val _selectedProject = MutableStateFlow<Project?>(null)
    val selectedProject: StateFlow<Project?> = _selectedProject.asStateFlow()

    // Language setting: "English" or "Nepali" (Support both languages requested)
    private val _appLanguage = MutableStateFlow("English") // "English" or "Nepali"
    val appLanguage: StateFlow<String> = _appLanguage.asStateFlow()

    // ------------------------------------------
    // OPERATION HANDLING
    // ------------------------------------------

    fun switchRole(role: String) {
        _currentUserRole.value = role
    }

    fun updateProfile(name: String, email: String) {
        _currentUserName.value = name
        _currentUserEmail.value = email
    }

    fun toggleLanguage() {
        _appLanguage.value = if (_appLanguage.value == "English") "Nepali" else "English"
    }

    fun selectProject(project: Project?) {
        _selectedProject.value = project
    }

    // Dynamic donation action
    fun makeDonation(
        donorName: String,
        amount: Double,
        category: String,
        paymentMethod: String,
        memo: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            
            // Insert donation log
            val donation = Donation(
                donorName = if (donorName.isBlank()) "Anonymous Donor" else donorName,
                amount = amount,
                category = category,
                date = dateStr,
                paymentMethod = paymentMethod,
                memo = memo
            )
            repository.insertDonation(donation)

            // Dynamic Sync: Write an Income financial record log automatically
            val financialRecord = FinancialRecord(
                type = "Income",
                title = "Donation received from $donorName",
                projectCategory = category,
                amount = amount,
                date = dateStr
            )
            repository.insertFinancialRecord(financialRecord)

            // Also, update associated project visual progress bar if category matches or is generic
            onSuccess()
        }
    }

    // Dynamic volunteer registration
    fun registerVolunteer(
        fullName: String,
        email: String,
        phone: String,
        skills: String,
        availability: String
    ) {
        viewModelScope.launch {
            val refCode = "FMN-${(1000..9999).random()}"
            val volunteer = Volunteer(
                fullName = fullName,
                email = email,
                phone = phone,
                skills = skills,
                availability = availability,
                hoursTracked = 0,
                isApproved = false, // Must be approved by administrator in admin settings!
                refCode = refCode
            )
            repository.insertVolunteer(volunteer)
            
            // Fetch all volunteers and set local registered state
            // Let's hold the latest registration
            _userVolunteerId.value = -1 // Mock index trigger to check state updates or look in Flow matching email.
        }
    }

    fun applyVacancy(vacancyId: Int) {
        viewModelScope.launch {
            repository.incrementVacancyApplications(vacancyId)
        }
    }

    // Dynamic scholarship application
    fun applyScholarship(
        applicantName: String,
        email: String,
        targetProgram: String,
        incomeBracket: String,
        currentSchool: String,
        whyNeeded: String,
        documentUploaded: String
    ) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val scholarship = Scholarship(
                applicantName = applicantName,
                email = email,
                targetProgram = targetProgram,
                incomeBracket = incomeBracket,
                currentSchool = currentSchool,
                whyNeeded = whyNeeded,
                documentUploaded = documentUploaded,
                status = "Pending",
                dateApplied = dateStr
            )
            repository.insertScholarship(scholarship)
            _userScholarshipId.value = -1 // Mark applied
        }
    }

    // Community ideas
    fun postIdea(title: String, content: String, category: String) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val newPost = ForumPost(
                authorName = _currentUserName.value,
                title = title,
                content = content,
                likes = 0,
                category = category,
                timestamp = "Just now"
            )
            repository.insertForumPost(newPost)
        }
    }

    fun likePost(postId: Int) {
        viewModelScope.launch {
            repository.triggerPostLike(postId)
        }
    }

    // ------------------------------------------
    // ADMIN FUNCTIONS
    // ------------------------------------------
    fun adminAddProject(
        title: String,
        description: String,
        budget: Double,
        location: String,
        beneficiaries: Int,
        teamLeads: String
    ) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val project = Project(
                title = title,
                description = description,
                budget = budget,
                currentSpent = 0.0,
                progressPercent = 0,
                location = location,
                beneficiaryCount = beneficiaries,
                teamMembers = teamLeads,
                startDate = dateStr,
                endDate = "TBD"
            )
            repository.insertProject(project)
        }
    }

    fun adminAddVacancy(title: String, type: String, category: String, description: String, deadline: String) {
        viewModelScope.launch {
            val vacancy = Vacancy(
                title = title,
                type = type,
                category = category,
                description = description,
                deadline = deadline
            )
            repository.insertVacancy(vacancy)
        }
    }

    fun adminAddFinancialRecord(type: String, title: String, category: String, amount: Double) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val record = FinancialRecord(
                type = type,
                title = title,
                projectCategory = category,
                amount = amount,
                date = dateStr
            )
            repository.insertFinancialRecord(record)
        }
    }

    fun adminApproveVolunteer(volId: Int, currentApproved: Boolean, mockHours: Int) {
        viewModelScope.launch {
            repository.updateVolunteerStatus(volId, approved = !currentApproved, hours = mockHours)
        }
    }

    fun adminUpdateScholarshipStatus(schId: Int, status: String) {
        viewModelScope.launch {
            repository.updateScholarshipStatus(schId, status)
        }
    }

    // ------------------------------------------
    // BOT CHAT GENERATION
    // ------------------------------------------
    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        
        val userMsg = ChatMessage(text = text, isUser = true)
        _chatMessages.value = _chatMessages.value + userMsg
        _isChatLoading.value = true

        viewModelScope.launch {
            // Build simple context history of past 4 turns to keep within safe window
            val history = _chatMessages.value.takeLast(5).dropLast(1).map { msg ->
                GeminiContent(
                    parts = listOf(GeminiPart(text = msg.text)),
                    role = if (msg.isUser) "user" else "model"
                )
            }
            
            val responseText = GeminiService.fetchResponse(text, history)
            _chatMessages.value = _chatMessages.value + ChatMessage(text = responseText, isUser = false)
            _isChatLoading.value = false
        }
    }
}

// ------------------------------------------
// VIEWMODEL FACTORY FOR ROOM DEPENDENCY
// ------------------------------------------
class NgoViewModelFactory(
    private val application: Application,
    private val repository: NgoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NgoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NgoViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
