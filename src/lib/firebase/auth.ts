import { 
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  signOut as firebaseSignOut,
  onAuthStateChanged,
  updateProfile,
  User,
  GoogleAuthProvider,
  signInWithPopup
} from 'firebase/auth'
import { doc, setDoc, getDoc } from 'firebase/firestore'
import { auth, firestore } from './config'
import { UserProfile } from '@/types'

const googleProvider = new GoogleAuthProvider()

export const authService = {
  // Sign in with email and password
  async signIn(email: string, password: string): Promise<User> {
    const result = await signInWithEmailAndPassword(auth, email, password)
    return result.user
  },

  // Sign up with email and password
  async signUp(email: string, password: string, displayName: string): Promise<User> {
    const result = await createUserWithEmailAndPassword(auth, email, password)
    const user = result.user

    // Update user profile
    await updateProfile(user, { displayName })

    // Create user profile document
    await this.createUserProfile(user, { displayName })

    return user
  },

  // Sign in with Google
  async signInWithGoogle(): Promise<User> {
    const result = await signInWithPopup(auth, googleProvider)
    const user = result.user

    // Check if user profile exists, create if not
    const userDoc = await getDoc(doc(firestore, 'users', user.uid))
    if (!userDoc.exists()) {
      await this.createUserProfile(user, {
        displayName: user.displayName || user.email?.split('@')[0] || 'User'
      })
    }

    return user
  },

  // Sign out
  async signOut(): Promise<void> {
    await firebaseSignOut(auth)
  },

  // Create user profile document
  async createUserProfile(user: User, additionalData: { displayName: string }): Promise<void> {
    const userProfile: Omit<UserProfile, 'id'> = {
      email: user.email!,
      displayName: additionalData.displayName,
      photoURL: user.photoURL,
      preferences: {
        theme: 'system',
        defaultShelf: 'currently-reading',
        privacyLevel: 'private'
      },
      createdAt: new Date(),
      lastActiveAt: new Date()
    }

    await setDoc(doc(firestore, 'users', user.uid), userProfile)
  },

  // Subscribe to auth state changes
  onAuthStateChanged(callback: (user: User | null) => void): () => void {
    return onAuthStateChanged(auth, callback)
  },

  // Get current user
  getCurrentUser(): User | null {
    return auth.currentUser
  }
}