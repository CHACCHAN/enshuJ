import { createRouter, createWebHistory } from 'vue-router';
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';
import { useAuth } from '../composables/useAuth';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'root',
            component: () => import('../views/index.vue')
        },
        {
            path: '/auth/login',
            name: 'login',
            component: () => import('../views/auth/LoginView.vue')
        },
        {
            path: '/auth/register',
            name: 'register',
            component: () => import('../views/auth/RegisterView.vue')
        },
        {
            path: '/shop',
            name: 'shop',
            component: () => import('../views/shop/ShopView.vue')
        },
        {
            path: '/order',
            name: 'order',
            component: () => import('../views/order/OrderView.vue')
        },
        {
            path: '/chat',
            component: () => import('../views/chat/ChatLayout.vue'),
            meta: { requiresAuth: true },
            children: [
                {
                    path: '',
                    name: 'chat-home',
                    component: () => import('../views/chat/ChatHome.vue'),
                },
                {
                    path: ':roomId',
                    name: 'chat-room',
                    component: () => import('../views/chat/ChatRoom.vue'),
                },
            ],
        },
        {
            path: '/profile',
            name: 'profile',
            component: () => import('../views/profile/ProfileView.vue'),
            meta: { requiresAuth: true },
        },
        {
            path: '/settings',
            name: 'account-settings',
            component: () => import('../views/profile/AccountSettings.vue'),
            meta: { requiresAuth: true },
        },
    ]
});

router.beforeEach(async (to) => {
    NProgress.start();
    if (to.meta.requiresAuth) {
        const { currentUser, authChecked, fetchCurrentUser } = useAuth();
        if (!authChecked.value) {
            await fetchCurrentUser();
        }
        if (!currentUser.value) {
            return '/auth/login';
        }
    }
});

router.afterEach(() => {
    NProgress.done();
});

export default router