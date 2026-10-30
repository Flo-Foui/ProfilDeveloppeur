package com.example.profildeveloppeur

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.profildeveloppeur.ui.theme.ProfilDeveloppeurTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ProfilDeveloppeurTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ProfileScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// Écran parent : il fournit les données et reçoit les événements.
@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    ProfileCard(
        name = "Camille Laurent",
        role = "Développeuse Android junior",
        description = "Passionnée par le développement mobile, " +
            "je crée des applications Android simples et accessibles.",
        skill1 = "Kotlin",
        skill2 = "Jetpack Compose",
        skill3 = "Git",
        contact = "camille.laurent@example.com",
        isAvailable = true,
        onContactClick = {
            // Plus tard : traiter la demande de contact.
        },
        onPortfolioClick = {
            // Plus tard : ouvrir le portfolio.
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp) // Padding externe : espace entre l'écran et la fiche.
    )
}

// Fiche réutilisable : les informations viennent de ses paramètres.
@Composable
fun ProfileCard(
    name: String,
    role: String,
    description: String,
    skill1: String,
    skill2: String,
    skill3: String,
    contact: String,
    isAvailable: Boolean,
    onContactClick: () -> Unit,
    onPortfolioClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFF0F4FF),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(20.dp) // Padding interne : espace dans la fiche.
        ) {
            ProfileHeader(
                name = name,
                role = role,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            ProfileDescription(
                description = description,
                modifier = Modifier.padding(bottom = 18.dp)
            )
            Text(
                text = "Compétences",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ProfileSkill(skill = skill1, modifier = Modifier.padding(bottom = 4.dp))
            ProfileSkill(skill = skill2, modifier = Modifier.padding(bottom = 4.dp))
            ProfileSkill(skill = skill3, modifier = Modifier.padding(bottom = 16.dp))
            ProfileAvailability(
                isAvailable = isAvailable,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            ProfileContact(
                contact = contact,
                modifier = Modifier.padding(bottom = 18.dp)
            )
            ContactButton(
                text = "Contacter",
                onClick = onContactClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )
            PortfolioLink(
                name = name,
                onClick = onPortfolioClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ProfileHeader(
    name: String,
    role: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = name,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF152238)
        )
        Text(
            text = role,
            fontSize = 17.sp,
            color = Color(0xFF315A90)
        )
    }
}

@Composable
fun ProfileDescription(
    description: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = description,
        modifier = modifier,
        color = Color(0xFF273449)
    )
}

@Composable
fun ProfileSkill(
    skill: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = "• $skill",
        modifier = modifier,
        color = Color(0xFF273449)
    )
}

@Composable
fun ProfileAvailability(
    isAvailable: Boolean,
    modifier: Modifier = Modifier
) {
    Text(
        text = if (isAvailable) {
            "Disponibilité : disponible pour un stage"
        } else {
            "Disponibilité : non disponible actuellement"
        },
        modifier = modifier,
        color = if (isAvailable) Color(0xFF176A3A) else Color(0xFF664B27)
    )
}

@Composable
fun ProfileContact(
    contact: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Contact : $contact",
        modifier = modifier,
        color = Color(0xFF273449)
    )
}

// Comme dans le TP 2, le bouton transmet le clic à son parent.
@Composable
fun ContactButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(text = text)
    }
}

// Zone cliquable personnalisée : rôle et description pour l'accessibilité.
@Composable
fun PortfolioLink(
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFDDE9FF),
        modifier = modifier
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
            .semantics {
                contentDescription = "Afficher le portfolio de $name"
            }
    ) {
        Text(
            text = "Voir le portfolio",
            modifier = Modifier.padding(14.dp),
            color = Color(0xFF1B4D91),
            fontWeight = FontWeight.Bold
        )
    }
}

// Preview 1 : profil Android disponible.
@Preview(name = "Profil Android junior", showBackground = true)
@Composable
fun ProfilePreviewAndroid() {
    ProfilDeveloppeurTheme {
        ProfileCard(
            name = "Camille Laurent",
            role = "Développeuse Android junior",
            description = "Je crée des applications Android avec Kotlin et Compose.",
            skill1 = "Kotlin",
            skill2 = "Jetpack Compose",
            skill3 = "Git",
            contact = "camille.laurent@example.com",
            isAvailable = true,
            onContactClick = {},
            onPortfolioClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

// Preview 2 : profil full-stack non disponible.
@Preview(name = "Profil full-stack", showBackground = true)
@Composable
fun ProfilePreviewFullStack() {
    ProfilDeveloppeurTheme {
        ProfileCard(
            name = "Alex Bernard",
            role = "Développeur full-stack",
            description = "Je développe des sites et des services web.",
            skill1 = "Kotlin",
            skill2 = "JavaScript",
            skill3 = "SQL",
            contact = "alex.bernard@example.com",
            isAvailable = false,
            onContactClick = {},
            onPortfolioClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}
