package com.example.data

import kotlinx.coroutines.flow.Flow

class NgoRepository(private val dao: NgoDao) {
    val allDonations: Flow<List<Donation>> = dao.getAllDonations()
    val allProjects: Flow<List<Project>> = dao.getAllProjects()
    val allVolunteers: Flow<List<Volunteer>> = dao.getAllVolunteers()
    val allVacancies: Flow<List<Vacancy>> = dao.getAllVacancies()
    val allScholarships: Flow<List<Scholarship>> = dao.getAllScholarships()
    val allForumPosts: Flow<List<ForumPost>> = dao.getAllForumPosts()
    val allFinancials: Flow<List<FinancialRecord>> = dao.getAllFinancials()

    suspend fun insertDonation(donation: Donation) = dao.insertDonation(donation)
    suspend fun insertProject(project: Project) = dao.insertProject(project)
    suspend fun getProjectById(id: Int): Project? = dao.getProjectById(id)
    suspend fun insertVolunteer(volunteer: Volunteer) = dao.insertVolunteer(volunteer)
    suspend fun updateVolunteerStatus(id: Int, approved: Boolean, hours: Int) = 
        dao.updateVolunteerStatus(id, approved, hours)
    suspend fun insertVacancy(vacancy: Vacancy) = dao.insertVacancy(vacancy)
    suspend fun incrementVacancyApplications(id: Int) = dao.incrementVacancyApplications(id)
    suspend fun insertScholarship(scholarship: Scholarship) = dao.insertScholarship(scholarship)
    suspend fun updateScholarshipStatus(id: Int, status: String) = 
        dao.updateScholarshipStatus(id, status)
    suspend fun insertForumPost(post: ForumPost) = dao.insertForumPost(post)
    suspend fun triggerPostLike(id: Int) = dao.triggerPostLike(id)
    suspend fun insertFinancialRecord(record: FinancialRecord) = dao.insertFinancialRecord(record)
}
