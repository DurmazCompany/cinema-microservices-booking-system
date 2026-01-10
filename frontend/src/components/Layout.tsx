import { Link, useNavigate, useLocation } from 'react-router-dom';

export default function Layout({ children }: { children: React.ReactNode }) {
    const navigate = useNavigate();
    const location = useLocation();
    const token = localStorage.getItem('cinema_token');

    const handleLogout = () => {
        localStorage.removeItem('cinema_token');
        navigate('/login');
    };

    // Don't show Navbar on login page
    if (location.pathname === '/login') {
        return <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">{children}</div>;
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <nav className="bg-indigo-600 text-white shadow-lg">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between h-16">
                        <div className="flex items-center">
                            <Link to="/showtimes" className="text-xl font-bold tracking-tight hover:text-indigo-100">
                                🎬 Cinema Microservices
                            </Link>
                            {token && (
                                <div className="ml-10 flex items-baseline space-x-4">
                                    <Link to="/showtimes" className="px-3 py-2 rounded-md hover:bg-indigo-700">Showtimes</Link>
                                    <Link to="/my-bookings" className="px-3 py-2 rounded-md hover:bg-indigo-700">My Bookings</Link>
                                    <Link to="/status" className="px-3 py-2 rounded-md hover:bg-indigo-700">System Status</Link>
                                </div>
                            )}
                        </div>
                        <div className="flex items-center gap-4">
                            {token ? (
                                <button
                                    onClick={handleLogout}
                                    className="bg-indigo-700 px-4 py-2 rounded-md text-sm hover:bg-indigo-800 transition-colors"
                                >
                                    Logout
                                </button>
                            ) : (
                                <Link
                                    to="/login"
                                    className="bg-white text-indigo-600 px-4 py-2 rounded-md text-sm font-bold hover:bg-gray-100 transition-colors"
                                >
                                    Login
                                </Link>
                            )}
                        </div>
                    </div>
                </div>
            </nav>
            <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
                {children}
            </main>
        </div>
    );
}
