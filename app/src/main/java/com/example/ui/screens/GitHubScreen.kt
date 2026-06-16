package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.network.GitHubApi
import com.example.network.GitHubRepo
import com.example.network.GitHubUser
import kotlinx.coroutines.launch
import retrofit2.HttpException

class GitHubViewModel : ViewModel() {
    var user by mutableStateOf<GitHubUser?>(null)
        private set
    var repos by mutableStateOf<List<GitHubRepo>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun fetchUser(username: String) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                user = GitHubApi.retrofitService.getUser(username)
                repos = GitHubApi.retrofitService.getRepos(username)
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    error = "User not found"
                } else {
                    error = "Network error: ${e.message()}"
                }
                user = null
                repos = emptyList()
            } catch (e: Exception) {
                error = "Error: ${e.localizedMessage}"
                user = null
                repos = emptyList()
            } finally {
                isLoading = false
            }
        }
    }
}

@Composable
fun GitHubScreen(viewModel: GitHubViewModel = viewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    
    // Seed initial behavior
    LaunchedEffect(Unit) {
        if (viewModel.user == null) {
            searchQuery = "gamerboyadarsh"
            viewModel.fetchUser(searchQuery)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp),
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("GitHub Username") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { if (searchQuery.isNotBlank()) viewModel.fetchUser(searchQuery) }) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
            },
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (viewModel.error != null) {
            Text(viewModel.error!!, color = MaterialTheme.colorScheme.error)
        } else if (viewModel.user != null) {
            GitHubUserProfile(viewModel.user!!, viewModel.repos)
        }
    }
}

@Composable
fun GitHubUserProfile(user: GitHubUser, repos: List<GitHubRepo>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = user.avatar_url,
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(100.dp).clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(user.name ?: user.login, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("@${user.login}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        GitHubStat("Repos", user.public_repos)
                        GitHubStat("Followers", user.followers)
                        GitHubStat("Following", user.following)
                    }
                }
            }
        }
        
        item {
            Text("Public Repositories", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        
        items(repos.take(10)) { repo -> // Display top 10 repos
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(repo.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("★ ${repo.stargazers_count}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                    if (!repo.description.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(repo.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (!repo.language.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(repo.language, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
fun GitHubStat(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
