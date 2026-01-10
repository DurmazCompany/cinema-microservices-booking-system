import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';

export default function Login() {
    const [isLogin, setIsLogin] = useState(true);
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        // Client-side validation
        if (password.length < 6) {
            setError('Password must be at least 6 characters long.');
            return;
        }

        setLoading(true);
        setError('');

        try {
            // Use config to get User Service URL explicitly if needed, but api/axios defaults to it.
            // However, for Auth verify the paths.
            // Register: POST /auth/register
            // Login: POST /auth/login

            const endpoint = isLogin ? '/auth/login' : '/auth/register';
            const payload = isLogin ? { email, password } : { email, password, role: 'USER' }; // Default role for register

            const response = await api.post(endpoint, payload);

            if (isLogin) {
                const token = response.data.token;
                localStorage.setItem('cinema_token', token);
                navigate('/showtimes');
            } else {
                // After register, switch to login tab or auto-login. Let's switch to login for clarity
                setIsLogin(true);
                setError('Registration successful! Please login.');
                setPassword('');
            }
        } catch (err: any) {
            console.error(err);
            setError(err.response?.data?.message || 'Authentication failed. Please check credentials or server status.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-md w-full space-y-8 bg-white p-8 rounded-xl shadow-2xl">
            <div>
                <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
                    {isLogin ? 'Sign in to Cinema' : 'Create Account'}
                </h2>
                <p className="mt-2 text-center text-sm text-gray-600">
                    Microservices Demo App
                </p>
            </div>

            <div className="flex justify-center space-x-4 border-b pb-4">
                <button
                    className={`px-4 py-2 font-medium ${isLogin ? 'text-indigo-600 border-b-2 border-indigo-600' : 'text-gray-500'}`}
                    onClick={() => setIsLogin(true)}
                >
                    Login
                </button>
                <button
                    className={`px-4 py-2 font-medium ${!isLogin ? 'text-indigo-600 border-b-2 border-indigo-600' : 'text-gray-500'}`}
                    onClick={() => setIsLogin(false)}
                >
                    Register
                </button>
            </div>

            <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
                {error && (
                    <div className="bg-red-50 text-red-700 p-3 rounded text-sm text-center">
                        {error}
                    </div>
                )}
                <div className="rounded-md shadow-sm -space-y-px">
                    <div>
                        <input
                            type="email"
                            required
                            className="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-t-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                            placeholder="Email address"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                        />
                    </div>
                    <div>
                        <input
                            type="password"
                            required
                            className="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-b-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                            placeholder="Password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </div>
                </div>

                <div>
                    <button
                        type="submit"
                        disabled={loading}
                        className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
                    >
                        {loading ? 'Processing...' : (isLogin ? 'Sign in' : 'Register')}
                    </button>
                </div>
            </form>
        </div>
    );
}
