package app.pwhs.universalinstaller.presentation.setting.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.pwhs.universalinstaller.R
import app.pwhs.universalinstaller.domain.model.InstallerProfile
import app.pwhs.universalinstaller.presentation.setting.SettingViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

@Composable
fun ProfileScreen(
    viewModel: SettingViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var editingProfile by remember { mutableStateOf<InstallerProfile?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<String?>(null) }

    ProfileUi(
        profiles = uiState.installerProfiles,
        onBack = { (context as? android.app.Activity)?.finish() },
        onAddProfile = {
            editingProfile = InstallerProfile(id = UUID.randomUUID().toString(), name = "")
        },
        onEditProfile = { editingProfile = it },
        onDeleteProfile = { showDeleteConfirm = it }
    )

    if (editingProfile != null) {
        ProfileEditDialog(
            profile = editingProfile!!,
            onDismiss = { editingProfile = null },
            onSave = {
                viewModel.saveProfile(it)
                editingProfile = null
            }
        )
    }

    if (showDeleteConfirm != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            title = { Text(stringResource(R.string.profile_delete_confirm_title)) },
            text = { Text(stringResource(R.string.profile_delete_confirm_msg)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteProfile(showDeleteConfirm!!)
                        showDeleteConfirm = null
                    }
                ) {
                    Text(stringResource(R.string.profile_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileUi(
    profiles: List<InstallerProfile>,
    onBack: () -> Unit,
    onAddProfile: () -> Unit,
    onEditProfile: (InstallerProfile) -> Unit,
    onDeleteProfile: (String) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            LargeTopAppBar(
                expandedHeight = 120.dp,
                title = {
                    Text(
                        text = stringResource(R.string.profile_list_title),
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back_cd),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProfile) {
                Icon(Icons.Rounded.Add, contentDescription = stringResource(R.string.profile_add_button))
            }
        }
    ) { innerPadding ->
        if (profiles.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.profile_empty_state),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(profiles, key = { it.id }) { profile ->
                    ProfileCard(
                        profile = profile,
                        onEdit = { onEditProfile(profile) },
                        onDelete = { onDeleteProfile(profile.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileCard(
    profile: InstallerProfile,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        ListItem(
            headlineContent = {
                Text(profile.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            },
            supportingContent = {
                val details = buildList {
                    profile.preferredBackend?.let { add(it) }
                    profile.installerPackageName?.let { add(it) }
                }.joinToString(" · ")
                if (details.isNotEmpty()) {
                    Text(details, style = MaterialTheme.typography.bodySmall)
                }
            },
            leadingContent = {
                Icon(
                    Icons.Rounded.Badge,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingContent = {
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Rounded.Edit, contentDescription = null)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Rounded.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            modifier = Modifier.clickable(onClick = onEdit)
        )
    }
}

@Composable
private fun ProfileEditDialog(
    profile: InstallerProfile,
    onDismiss: () -> Unit,
    onSave: (InstallerProfile) -> Unit,
) {
    var name by remember { mutableStateOf(profile.name) }
    var installerPkg by remember { mutableStateOf(profile.installerPackageName ?: "") }
    var backend by remember { mutableStateOf(profile.preferredBackend ?: "Default") }
    
    // Flags
    var replaceExisting by remember { mutableStateOf(profile.replaceExisting) }
    var allowTest by remember { mutableStateOf(profile.allowTest) }
    var requestDowngrade by remember { mutableStateOf(profile.requestDowngrade) }
    var grantAllPermissions by remember { mutableStateOf(profile.grantAllPermissions) }
    var bypassLowTargetSdk by remember { mutableStateOf(profile.bypassLowTargetSdk) }
    var allUsers by remember { mutableStateOf(profile.allUsers) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (profile.name.isEmpty()) stringResource(R.string.profile_create_title) else stringResource(R.string.profile_edit_title)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.profile_name_label)) },
                    placeholder = { Text(stringResource(R.string.profile_name_placeholder)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = installerPkg,
                    onValueChange = { installerPkg = it },
                    label = { Text(stringResource(R.string.setting_shizuku_installer_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(stringResource(R.string.setting_shizuku_backend), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Column {
                    listOf("Default", "Shizuku", "Root").forEach { b ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { backend = b }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(selected = backend == b, onClick = { backend = b })
                            Text(b, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }

                Text(stringResource(R.string.dialog_menu_title), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                
                ProfileFlagRow(stringResource(R.string.setting_shizuku_replace), replaceExisting) { replaceExisting = it }
                ProfileFlagRow(stringResource(R.string.setting_shizuku_allow_test), allowTest) { allowTest = it }
                ProfileFlagRow(stringResource(R.string.setting_shizuku_downgrade), requestDowngrade) { requestDowngrade = it }
                ProfileFlagRow(stringResource(R.string.setting_shizuku_grant_permissions), grantAllPermissions) { grantAllPermissions = it }
                ProfileFlagRow(stringResource(R.string.setting_shizuku_bypass_sdk), bypassLowTargetSdk) { bypassLowTargetSdk = it }
                ProfileFlagRow(stringResource(R.string.setting_shizuku_all_users), allUsers) { allUsers = it }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        profile.copy(
                            name = name,
                            installerPackageName = installerPkg.ifBlank { null },
                            preferredBackend = if (backend == "Default") null else backend,
                            replaceExisting = replaceExisting,
                            allowTest = allowTest,
                            requestDowngrade = requestDowngrade,
                            grantAllPermissions = grantAllPermissions,
                            bypassLowTargetSdk = bypassLowTargetSdk,
                            allUsers = allUsers,
                        )
                    )
                },
                enabled = name.isNotBlank()
            ) {
                Text(stringResource(R.string.profile_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun ProfileFlagRow(
    label: String,
    checked: Boolean?,
    onCheckedChange: (Boolean?) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onCheckedChange(
                    when (checked) {
                        true -> false
                        false -> null
                        null -> true
                    }
                )
            }
            .padding(vertical = 4.dp)
    ) {
        val state = when (checked) {
            true -> androidx.compose.ui.state.ToggleableState.On
            false -> androidx.compose.ui.state.ToggleableState.Off
            null -> androidx.compose.ui.state.ToggleableState.Indeterminate
        }
        androidx.compose.material3.TriStateCheckbox(
            state = state,
            onClick = null
        )
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp),
            color = if (checked == null) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
        )
    }
}
