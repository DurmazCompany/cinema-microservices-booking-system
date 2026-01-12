export const config = {
    services: {
        // Production URLs from Render (hardcoded for now)
        // If running locally, these will fail and you should use localhost instead
        user: import.meta.env.VITE_USER_SERVICE_URL ||
            (import.meta.env.MODE === 'production'
                ? 'https://user-service-zcw6.onrender.com'
                : 'http://localhost:8085'),

        catalog: import.meta.env.VITE_CATALOG_SERVICE_URL ||
            (import.meta.env.MODE === 'production'
                ? 'https://catalog-service-msei.onrender.com'
                : 'http://localhost:8082'),

        seat: import.meta.env.VITE_SEAT_SERVICE_URL ||
            (import.meta.env.MODE === 'production'
                ? 'https://seat-service-1qck.onrender.com'
                : 'http://localhost:8083'),

        booking: import.meta.env.VITE_BOOKING_SERVICE_URL ||
            (import.meta.env.MODE === 'production'
                ? 'https://booking-service-lhk5.onrender.com'
                : 'http://localhost:8084')
    },

    getServiceUrl: (serviceName: 'user' | 'catalog' | 'seat' | 'booking') => {
        return config.services[serviceName];
    }
};
