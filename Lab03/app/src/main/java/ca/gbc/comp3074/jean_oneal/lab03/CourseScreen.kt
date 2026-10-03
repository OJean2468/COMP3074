package ca.gbc.comp3074.jean_oneal.lab03

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android

@Composable
fun CourseScreen() {
    val context = LocalContext.current

    val courses = remember {
        mutableStateListOf<Course>().apply {
            addAll(loadCourses(context))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Courses",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Button(
                onClick = {
                    val courseNumber = courses.size + 1
                    val newCourse = Course(
                        code = "COMP$courseNumber",
                        name = "New course $courseNumber"
                    )
                    courses.add(newCourse)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Course"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Add Course")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyCourseList(courses = courses)
    }
}

@Composable
fun CourseItem(course: Course) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Android,
            contentDescription = "Android Icon",
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = course.code,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = course.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun ColumnScope.LazyCourseList(courses: List<Course>) {
    val listState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
    ) {
        LazyCourseListWithState(listState = listState, courses = courses)
    }
}

@Composable
fun LazyCourseListWithState(listState: LazyListState, courses: List<Course>) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 8.dp)
        ) {
            items(courses) { course ->
                CourseItem(course = course)
                HorizontalDivider()
            }
        }

        // Scrollbar
        BoxWithConstraints(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(6.dp)
                .padding(vertical = 4.dp)
        ) {
            val scrollbarState by remember {
                derivedStateOf {
                    val layoutInfo = listState.layoutInfo
                    val totalItemsCount = layoutInfo.totalItemsCount
                    val visibleItemsInfo = layoutInfo.visibleItemsInfo

                    if (totalItemsCount > 0 && visibleItemsInfo.isNotEmpty()) {
                        val firstVisibleItemIndex = listState.firstVisibleItemIndex
                        val firstItem = visibleItemsInfo.firstOrNull()
                        val offset = firstItem?.offset ?: 0
                        val size = firstItem?.size ?: 1
                        val fractionalIndex = firstVisibleItemIndex.toFloat() - (offset.toFloat() / size.toFloat())
                        val maxScrollableItems = (totalItemsCount - visibleItemsInfo.size).coerceAtLeast(1)
                        val progress = (fractionalIndex / maxScrollableItems.toFloat()).coerceIn(0f, 1f)
                        val thumbFraction = (visibleItemsInfo.size.toFloat() / totalItemsCount.toFloat()).coerceIn(0.15f, 1f)
                        Triple(progress, thumbFraction, true)
                    } else {
                        Triple(0f, 1f, false)
                    }
                }
            }

            if (scrollbarState.third) {
                val (scrollProgress, thumbHeightFraction, _) = scrollbarState
                val trackHeightPx = constraints.maxHeight.toFloat()
                val thumbHeightPx = trackHeightPx * thumbHeightFraction
                val maxOffsetPx = trackHeightPx - thumbHeightPx
                val thumbOffsetYPx = maxOffsetPx * scrollProgress

                val density = LocalDensity.current
                val offsetYDp = with(density) { thumbOffsetYPx.toDp() }

                // Track
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray.copy(alpha = 0.3f), shape = RoundedCornerShape(3.dp))
                )

                // Thumb
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(with(density) { thumbHeightPx.toDp() })
                        .offset(y = offsetYDp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(3.dp)
                        )
                )
            }
        }
    }
}
