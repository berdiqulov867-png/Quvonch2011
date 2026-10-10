package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserRole
import com.example.ui.components.AppHeader
import com.example.ui.components.BilimCard
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.admin.StudentManagementScreen
import com.example.ui.screens.admin.SubjectManagementScreen
import com.example.ui.screens.admin.TeacherManagementScreen
import com.example.ui.screens.attendance.AttendanceScreen
import com.example.ui.screens.grades.GradeJournalScreen
import com.example.ui.screens.library.DigitalLibraryScreen
import com.example.ui.screens.parent.ParentPortalScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.rankings.RankingsAndReportsScreen
import com.example.ui.screens.tests_homework.TestsAndHomeworkScreen
import com.example.ui.screens.timetable.TimetableScreen
import com.example.ui.theme.BlueGeography
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.DarkNavyCardBorder
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.GoldHistory
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.flow.collectLatest

enum class ScreenNav(val titleUz: String, val icon: ImageVector) {
    DASHBOARD("Bosh sahifa", Icons.Default.Dashboard),
    STUDENTS("O'quvchilar", Icons.Default.School),
    TEACHERS("Ustozlar", Icons.Default.People),
    GRADES("Jurnal", Icons.Default.Grade),
    ATTENDANCE("Davomat", Icons.Default.HowToReg),
    TESTS_HOMEWORK("Vazifalar", Icons.Default.Assignment),
    LIBRARY("Kutubxona", Icons.Default.LocalLibrary),
    TIMETABLE("Jadval", Icons.Default.EventNote),
    RANKINGS("Reyting", Icons.Default.Leaderboard),
    SUBJECTS("Fanlar", Icons.Default.Class),
    PARENT_CABINET("Kabinet", Icons.Default.FamilyRestroom),
    MORE("Ko'proq", Icons.Default.MoreHoriz),
    PROFILE("Profil", Icons.Default.Person)
}

@Composable
fun BilimMarkaziApp(
    viewModel: BilimViewModel,
    modifier: Modifier = Modifier
) {
    var showSplash by remember { mutableStateOf(true) }
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.actionMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    if (showSplash) {
        SplashScreen(onSplashFinished = { showSplash = false })
        return
    }

    if (currentUser == null) {
        LoginScreen(viewModel = viewModel)
        return
    }

    val userRole = currentUser?.role ?: UserRole.STUDENT.name
    var currentScreen by remember(userRole) {
        mutableStateOf(
            when (userRole) {
                UserRole.ADMIN.name -> ScreenNav.DASHBOARD
                UserRole.TEACHER.name -> ScreenNav.GRADES
                UserRole.STUDENT.name -> ScreenNav.GRADES
                UserRole.PARENT.name -> ScreenNav.PARENT_CABINET
                else -> ScreenNav.DASHBOARD
            }
        )
    }

    BackHandler(enabled = currentScreen != ScreenNav.DASHBOARD && currentScreen != ScreenNav.PARENT_CABINET && currentScreen != ScreenNav.GRADES) {
        currentScreen = when (userRole) {
            UserRole.ADMIN.name -> ScreenNav.DASHBOARD
            UserRole.PARENT.name -> ScreenNav.PARENT_CABINET
            else -> ScreenNav.GRADES
        }
    }

    val navItems = when (userRole) {
        UserRole.ADMIN.name -> listOf(
            ScreenNav.DASHBOARD,
            ScreenNav.STUDENTS,
            ScreenNav.TEACHERS,
            ScreenNav.GRADES,
            ScreenNav.MORE
        )
        UserRole.TEACHER.name -> listOf(
            ScreenNav.GRADES,
            ScreenNav.ATTENDANCE,
            ScreenNav.TESTS_HOMEWORK,
            ScreenNav.TIMETABLE,
            ScreenNav.MORE
        )
        UserRole.STUDENT.name -> listOf(
            ScreenNav.GRADES,
            ScreenNav.ATTENDANCE,
            ScreenNav.TESTS_HOMEWORK,
            ScreenNav.TIMETABLE,
            ScreenNav.MORE
        )
        UserRole.PARENT.name -> listOf(
            ScreenNav.PARENT_CABINET,
            ScreenNav.TIMETABLE,
            ScreenNav.LIBRARY,
            ScreenNav.MORE
        )
        else -> listOf(ScreenNav.DASHBOARD, ScreenNav.GRADES, ScreenNav.MORE)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(DarkNavyBg)) {
        val isWideScreen = maxWidth >= 600.dp

        if (isWideScreen) {
            // Tablet / Desktop layout with Navigation Rail
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    containerColor = DarkNavySurface,
                    contentColor = TextWhite,
                    header = {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .padding(top = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "Bilim Markazi",
                                tint = GoldHistory,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    navItems.forEach { item ->
                        val isSelected = currentScreen == item
                        NavigationRailItem(
                            selected = isSelected,
                            onClick = { currentScreen = item },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.titleUz
                                )
                            },
                            label = { Text(item.titleUz, fontSize = 11.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = DarkNavyBg,
                                selectedTextColor = GoldHistory,
                                indicatorColor = GoldHistory,
                                unselectedIconColor = TextMutedGray,
                                unselectedTextColor = TextMutedGray
                            )
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    AppHeader(
                        user = currentUser,
                        onLogout = { viewModel.logout() }
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        ScreenContent(
                            screen = currentScreen,
                            viewModel = viewModel,
                            onNavigateTo = { currentScreen = it }
                        )
                    }
                }
            }
        } else {
            // Mobile layout with Bottom Navigation
            Scaffold(
                containerColor = DarkNavyBg,
                topBar = {
                    AppHeader(
                        user = currentUser,
                        onLogout = { viewModel.logout() }
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = DarkNavySurface,
                        contentColor = TextWhite,
                        tonalElevation = 8.dp
                    ) {
                        navItems.forEach { item ->
                            val isSelected = currentScreen == item
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { currentScreen = item },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.titleUz
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.titleUz,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = DarkNavyBg,
                                    selectedTextColor = GoldHistory,
                                    indicatorColor = GoldHistory,
                                    unselectedIconColor = TextMutedGray,
                                    unselectedTextColor = TextMutedGray
                                ),
                                modifier = Modifier.testTag("nav_${item.name.lowercase()}")
                            )
                        }
                    }
                },
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    ScreenContent(
                        screen = currentScreen,
                        viewModel = viewModel,
                        onNavigateTo = { currentScreen = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun ScreenContent(
    screen: ScreenNav,
    viewModel: BilimViewModel,
    onNavigateTo: (ScreenNav) -> Unit
) {
    Crossfade(targetState = screen, label = "screen_transition") { target ->
        when (target) {
            ScreenNav.DASHBOARD -> AdminDashboardScreen(
                viewModel = viewModel,
                onNavigateToStudents = { onNavigateTo(ScreenNav.STUDENTS) },
                onNavigateToTeachers = { onNavigateTo(ScreenNav.TEACHERS) },
                onNavigateToGrades = { onNavigateTo(ScreenNav.GRADES) },
                onNavigateToTimetable = { onNavigateTo(ScreenNav.TIMETABLE) },
                onNavigateToAttendance = { onNavigateTo(ScreenNav.ATTENDANCE) }
            )
            ScreenNav.STUDENTS -> StudentManagementScreen(viewModel = viewModel)
            ScreenNav.TEACHERS -> TeacherManagementScreen(viewModel = viewModel)
            ScreenNav.GRADES -> GradeJournalScreen(viewModel = viewModel)
            ScreenNav.ATTENDANCE -> AttendanceScreen(viewModel = viewModel)
            ScreenNav.TESTS_HOMEWORK -> TestsAndHomeworkScreen(viewModel = viewModel)
            ScreenNav.LIBRARY -> DigitalLibraryScreen(viewModel = viewModel)
            ScreenNav.TIMETABLE -> TimetableScreen(viewModel = viewModel)
            ScreenNav.RANKINGS -> RankingsAndReportsScreen(viewModel = viewModel)
            ScreenNav.SUBJECTS -> SubjectManagementScreen(viewModel = viewModel)
            ScreenNav.PARENT_CABINET -> ParentPortalScreen(viewModel = viewModel)
            ScreenNav.PROFILE -> ProfileScreen(
                viewModel = viewModel,
                onLogout = { viewModel.logout() }
            )
            ScreenNav.MORE -> MoreMenuScreen(
                viewModel = viewModel,
                onNavigateTo = onNavigateTo
            )
        }
    }
}

@Composable
private fun MoreMenuScreen(
    viewModel: BilimViewModel,
    onNavigateTo: (ScreenNav) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isTeacher = currentUser?.role == UserRole.TEACHER.name
    val isStudent = currentUser?.role == UserRole.STUDENT.name
    val isParent = currentUser?.role == UserRole.PARENT.name

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Qo'shimcha Bo'limlar",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
        Text(
            text = "Maktab boshqaruvi va o'quv resurslari",
            fontSize = 12.sp,
            color = TextMutedGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        val menuItems = mutableListOf<Pair<ScreenNav, String>>()

        if (!isStudent && !isTeacher && !isParent) {
            menuItems.add(ScreenNav.SUBJECTS to "Tarix va Geografiya fanlarini boshqarish")
            menuItems.add(ScreenNav.ATTENDANCE to "Umummaktab davomat jurnali")
            menuItems.add(ScreenNav.TESTS_HOMEWORK to "Testlar va uy vazifalar banki")
        }

        menuItems.add(ScreenNav.LIBRARY to "Elektron darsliklar va tarixiy atlaslar")
        menuItems.add(ScreenNav.TIMETABLE to "Haftalik dars soatlari jadvali")
        menuItems.add(ScreenNav.RANKINGS to "O'quvchilar va sinflar reytingi")
        menuItems.add(ScreenNav.PROFILE to "Shaxsiy profil va parolni yangilash")

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            menuItems.forEach { (nav, subtitle) ->
                BilimCard(onClick = { onNavigateTo(nav) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(GoldHistory.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = nav.icon,
                                contentDescription = null,
                                tint = GoldHistory,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = nav.titleUz,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = subtitle,
                                fontSize = 12.sp,
                                color = TextMutedGray
                            )
                        }

                        Text(
                            text = "→",
                            fontSize = 16.sp,
                            color = GoldHistory
                        )
                    }
                }
            }
        }
    }
}
