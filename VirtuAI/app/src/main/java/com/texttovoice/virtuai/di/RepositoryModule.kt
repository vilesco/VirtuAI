package com.texttovoice.virtuai.di

import com.texttovoice.virtuai.data.repository.*
import com.texttovoice.virtuai.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun provideChatRepository(chatRepositoryImpl: ChatRepositoryImpl): ChatRepository

    @Binds
    abstract fun provideConversationRepository(conversationRepositoryImpl: ConversationRepositoryImpl): ConversationRepository

    @Binds
    abstract fun provideMessageRepository(messageRepositoryImpl: MessageRepositoryImpl): MessageRepository

    @Binds
    abstract fun providePreferenceRepository(preferenceRepositoryImpl: PreferenceRepositoryImpl): PreferenceRepository

    @Binds
    abstract fun provideFirebaseRepository(firebaseRepositoryImpl: FirebaseRepositoryImpl): FirebaseRepository

    @Binds
    abstract fun provideImageRepository(imageRepositoryImpl: ImageRepositoryImpl): ImageRepository

    @Binds
    abstract fun provideAudioRepository(audioRepositoryImpl: AudioRepositoryImpl): AudioRepository

}