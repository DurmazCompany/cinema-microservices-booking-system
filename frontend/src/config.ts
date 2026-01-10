export const config = {
    // If Vite env variable is set (Gateway mode), use it. otherwise use direct service URLs.
    apiBaseUrl: import.meta.env.VITE_API_BASE_URL || '',

    // Direct service URLs (fallbacks if Gateway is not used)
    services: {
        user: 'http://localhost:8085', // Note: Using 8085 as per resolution
        catalog: 'http://localhost:8082',
        seat: 'http://localhost:8083',
        booking: 'http://localhost:8084'
    },

    getServiceUrl: (serviceName: 'user' | 'catalog' | 'seat' | 'booking') => {
        // If Gateway URL is provided, all requests go there (e.g., http://localhost:8080/user-service/...)
        // However, for this simple demo without full Gateway routing setup in frontend, 
        // we will prioritize direct service access if specific logic isn't implemented.
        // Given the prompt requirements:

        if (import.meta.env.VITE_API_BASE_URL) {
            // Assuming Gateway pattern: GATEWAY_URL/service-name
            // But usually microservices behind gateway have prefixes. 
            // For simplicity in this demo, if API_BASE_URL is set, we treat it as a single entry point 
            // assuming the backend gateway handles routing (e.g. /auth handled by user-service via gateway).
            // Adjust logic as needed. For now, strictly following prompt:
            return import.meta.env.VITE_API_BASE_URL;
        }

        return config.services[serviceName];
    }
};
