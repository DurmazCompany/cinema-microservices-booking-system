import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { seatApi, bookingApi } from '../api/axios';
import Loading from '../components/Loading';

interface Seat {
    seatNumber: number;
    status: 'AVAILABLE' | 'LOCKED' | 'SOLD';
    lockedUntil?: string;
}

export default function Seats() {
    const { showtimeId } = useParams();
    const [seats, setSeats] = useState<Seat[]>([]);
    const [selectedSeat, setSelectedSeat] = useState<number | null>(null);
    const [loading, setLoading] = useState(true);
    const [bookingLoading, setBookingLoading] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        fetchSeats();
    }, [showtimeId]);

    const fetchSeats = async () => {
        try {
            const response = await seatApi.get(`/seats/${showtimeId}`);
            // Ensure seats are sorted
            const sortedSeats = (response.data as Seat[]).sort((a, b) => a.seatNumber - b.seatNumber);
            setSeats(sortedSeats);
        } catch (err) {
            setError('Failed to load seats. Is Seat Service running?');
        } finally {
            setLoading(false);
        }
    };

    const handleBook = async () => {
        if (!selectedSeat) return;
        setBookingLoading(true);

        try {
            // Booking Service URL
            await bookingApi.post('/bookings', {
                showtimeId: Number(showtimeId),
                seatNumber: selectedSeat
            });

            alert('Booking Confirmed! ✅');
            navigate('/my-bookings');
        } catch (err: any) {
            console.error(err);
            if (err.response?.status === 409) {
                alert('Seat already taken! Please choose another.');
                fetchSeats(); // Refresh seats
            } else if (err.response?.status === 401 || err.response?.status === 403) {
                alert('Authentication failed. Please Logout and Login again.');
                console.log('Auth error:', err);
                // navigate('/login'); // Optional: auto-redirect
            } else {
                alert(`Booking failed. Error ${err.response?.status}: ${err.response?.data?.message || err.message || 'Unknown Server Error'}`);
            }
        } finally {
            setBookingLoading(false);
        }
    };

    if (loading) return <Loading />;
    if (error) return <div className="text-center text-red-600 mt-10">{error}</div>;

    return (
        <div className="max-w-4xl mx-auto">
            <div className="mb-8 flex justify-between items-center">
                <h1 className="text-2xl font-bold text-gray-900">Select Seats (Showtime #{showtimeId})</h1>
                <div className="flex gap-4 text-sm">
                    <span className="flex items-center"><div className="w-4 h-4 bg-white border border-gray-300 rounded mr-2"></div> Available</span>
                    <span className="flex items-center"><div className="w-4 h-4 bg-green-500 rounded mr-2"></div> Selected</span>
                    <span className="flex items-center"><div className="w-4 h-4 bg-gray-400 rounded mr-2"></div> Taken</span>
                </div>
            </div>

            <div className="bg-white p-8 rounded-lg shadow-lg mb-8">
                <div className="w-full h-16 bg-gradient-to-b from-gray-200 to-white transform -perspective-x rounded-lg mb-12 flex items-center justify-center text-gray-400 text-sm font-semibold tracking-widest shadow-inner border-t-4 border-gray-300">
                    SCREEN
                </div>

                <div className="grid grid-cols-6 sm:grid-cols-8 md:grid-cols-10 gap-4 justify-items-center">
                    {seats.map((seat) => {
                        const isTaken = seat.status === 'SOLD' || seat.status === 'LOCKED';
                        const isSelected = selectedSeat === seat.seatNumber;

                        return (
                            <button
                                key={seat.seatNumber}
                                disabled={isTaken}
                                onClick={() => setSelectedSeat(seat.seatNumber)}
                                className={`
                  w-12 h-12 m-1 rounded-t-lg transition-all duration-200 flex items-center justify-center font-bold text-sm
                  ${isTaken
                                        ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                                        : isSelected
                                            ? 'bg-green-500 text-white transform scale-110 ring-4 ring-green-200 z-10'
                                            : 'bg-indigo-100 text-indigo-700 hover:bg-indigo-200 hover:scale-105 hover:shadow-md'}
                `}
                            >
                                {seat.seatNumber}
                            </button>
                        );
                    })}
                </div>
            </div>

            <div className="fixed bottom-0 left-0 right-0 bg-white border-t p-4 shadow-lg md:relative md:bg-transparent md:border-0 md:shadow-none md:p-0">
                <div className="max-w-4xl mx-auto flex justify-between items-center bg-white md:p-6 md:rounded-lg md:shadow-md">
                    <div>
                        <p className="text-gray-500">Selected Seat:</p>
                        <p className="text-2xl font-bold text-indigo-600">
                            {selectedSeat ? `#${selectedSeat}` : '-'}
                        </p>
                    </div>
                    <button
                        disabled={!selectedSeat || bookingLoading}
                        onClick={handleBook}
                        className="bg-indigo-600 text-white px-8 py-3 rounded-lg font-bold text-lg hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed shadow-md"
                    >
                        {bookingLoading ? 'Booking...' : 'Book Ticket'}
                    </button>
                </div>
            </div>
        </div>
    );
}
