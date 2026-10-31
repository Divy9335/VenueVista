import { useEffect, useMemo, useState } from 'react'
import './App.css'

const API_TOKEN_KEY = 'venuevista-token'
const API_USER_KEY = 'venuevista-user'

async function apiRequest(path, { token, ...options } = {}) {
  const response = await fetch(`/api${path}`, {
    ...options,
    headers: {
      ...(options.body ? { 'Content-Type': 'application/json' } : {}),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers,
    },
  })

  const body = await response.json().catch(() => null)
  if (!response.ok) {
    const message = body?.message || body?.error || `Request failed (${response.status})`
    throw new Error(message)
  }
  return body?.data ?? body
}

function formatDate(value) {
  if (!value) return 'Date to be announced'
  return new Intl.DateTimeFormat(undefined, {
    weekday: 'short',
    month: 'long',
    day: 'numeric',
    year: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
  }).format(new Date(value))
}

function formatPrice(value) {
  return new Intl.NumberFormat(undefined, {
    style: 'currency',
    currency: 'KES',
    maximumFractionDigits: 0,
  }).format(Number(value) || 0)
}

function EventCard({ event, onSelect }) {
  const venueName = event.venue?.name || 'Venue details available soon'
  const city = event.venue?.city

  return (
    <article className="event-card">
      <div className="event-card-art" aria-hidden="true">
        <span className="art-orbit orbit-one" />
        <span className="art-orbit orbit-two" />
        <span className="art-star">✳</span>
        <span className="art-index">{String(event.id).padStart(2, '0')}</span>
        <span className="art-category">LIVE EVENT</span>
      </div>
      <div className="event-card-body">
        <p className="event-date">{formatDate(event.startTime)}</p>
        <h3>{event.title}</h3>
        <p className="event-description">{event.description}</p>
        <div className="event-card-footer">
          <div>
            <p className="venue-name">{venueName}</p>
            <p className="venue-city">{city || 'Location details'}</p>
          </div>
          <button className="text-button" onClick={() => onSelect(event)}>
            Tickets <span aria-hidden="true">↗</span>
          </button>
        </div>
        <p className="starting-price">From {formatPrice(event.basePrice)}</p>
      </div>
    </article>
  )
}

function AuthDialog({ onClose, onAuthenticated }) {
  const [mode, setMode] = useState('login')
  const [form, setForm] = useState({ fullName: '', email: '', password: '' })
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  async function submit(event) {
    event.preventDefault()
    setError('')
    setBusy(true)
    try {
      const endpoint = mode === 'login' ? '/auth/login' : '/auth/register'
      const request = mode === 'login'
        ? { email: form.email, password: form.password }
        : { ...form, role: 'CUSTOMER' }
      const user = await apiRequest(endpoint, {
        method: 'POST',
        body: JSON.stringify(request),
      })
      onAuthenticated(user)
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="dialog-backdrop" role="presentation" onMouseDown={(event) => {
      if (event.target === event.currentTarget) onClose()
    }}>
      <section className="dialog auth-dialog" role="dialog" aria-modal="true" aria-labelledby="auth-title">
        <button className="dialog-close" onClick={onClose} aria-label="Close dialog">×</button>
        <p className="eyebrow">YOUR NEXT NIGHT OUT</p>
        <h2 id="auth-title">{mode === 'login' ? 'Welcome back.' : 'Join the guest list.'}</h2>
        <p className="dialog-intro">
          {mode === 'login' ? 'Sign in to reserve your seats.' : 'Create a customer account to book tickets.'}
        </p>
        <form className="stacked-form" onSubmit={submit}>
          {mode === 'register' && (
            <label>
              Full name
              <input
                autoComplete="name"
                required
                value={form.fullName}
                onChange={(event) => setForm({ ...form, fullName: event.target.value })}
              />
            </label>
          )}
          <label>
            Email address
            <input
              autoComplete="email"
              type="email"
              required
              value={form.email}
              onChange={(event) => setForm({ ...form, email: event.target.value })}
            />
          </label>
          <label>
            Password
            <input
              autoComplete={mode === 'login' ? 'current-password' : 'new-password'}
              type="password"
              required
              minLength={mode === 'register' ? 8 : undefined}
              value={form.password}
              onChange={(event) => setForm({ ...form, password: event.target.value })}
            />
          </label>
          {error && <p className="form-error" role="alert">{error}</p>}
          <button className="primary-button full-button" disabled={busy}>
            {busy ? 'Please wait…' : mode === 'login' ? 'Sign in' : 'Create account'}
          </button>
        </form>
        <p className="auth-switch">
          {mode === 'login' ? 'New here?' : 'Already have an account?'}{' '}
          <button onClick={() => { setMode(mode === 'login' ? 'register' : 'login'); setError('') }}>
            {mode === 'login' ? 'Create an account' : 'Sign in'}
          </button>
        </p>
      </section>
    </div>
  )
}

function BookingDialog({ event, token, onClose, onBooked, onSignIn }) {
  const [seats, setSeats] = useState([])
  const [selected, setSelected] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  useEffect(() => {
    let active = true
    apiRequest(`/events/${event.id}/seats`)
      .then((result) => {
        if (active) setSeats(Array.isArray(result) ? result : [])
      })
      .catch((requestError) => {
        if (active) setError(requestError.message)
      })
      .finally(() => {
        if (active) setLoading(false)
      })
    return () => { active = false }
  }, [event.id])

  const total = seats
    .filter((seat) => selected.includes(seat.eventSeatId))
    .reduce((sum, seat) => sum + Number(seat.price || 0), 0)

  function toggleSeat(seat) {
    if (seat.status !== 'AVAILABLE') return
    setSelected((current) => current.includes(seat.eventSeatId)
      ? current.filter((id) => id !== seat.eventSeatId)
      : [...current, seat.eventSeatId])
  }

  async function bookSeats() {
    if (!token) {
      onSignIn()
      return
    }
    if (!selected.length) return
    setBusy(true)
    setError('')
    try {
      const booking = await apiRequest('/bookings', {
        method: 'POST',
        token,
        body: JSON.stringify({ eventId: event.id, eventSeatIds: selected }),
      })
      onBooked(booking)
    } catch (requestError) {
      setError(requestError.message)
      setBusy(false)
    }
  }

  const rows = useMemo(() => {
    const grouped = new Map()
    for (const seat of seats) {
      const row = seat.rowLabel || '?'
      grouped.set(row, [...(grouped.get(row) || []), seat])
    }
    return [...grouped.entries()].sort(([a], [b]) => a.localeCompare(b))
  }, [seats])

  return (
    <div className="dialog-backdrop" role="presentation" onMouseDown={(click) => {
      if (click.target === click.currentTarget) onClose()
    }}>
      <section className="dialog booking-dialog" role="dialog" aria-modal="true" aria-labelledby="booking-title">
        <button className="dialog-close" onClick={onClose} aria-label="Close dialog">×</button>
        <p className="eyebrow">MAKE IT A NIGHT</p>
        <h2 id="booking-title">{event.title}</h2>
        <p className="dialog-intro">{formatDate(event.startTime)} · {event.venue?.name || 'Venue'}</p>
        {loading ? <p className="loading-message">Finding your seats…</p> : (
          <>
            <div className="stage">STAGE</div>
            <div className="seat-map" aria-label="Choose available seats">
              {rows.map(([row, rowSeats]) => (
                <div className="seat-row" key={row}>
                  <span className="row-label">{row}</span>
                  {rowSeats.sort((a, b) => a.seatNumber - b.seatNumber).map((seat) => {
                    const isSelected = selected.includes(seat.eventSeatId)
                    const isAvailable = seat.status === 'AVAILABLE'
                    return (
                      <button
                        key={seat.eventSeatId}
                        className={`seat ${isSelected ? 'selected' : ''} ${!isAvailable ? 'unavailable' : ''}`}
                        disabled={!isAvailable}
                        onClick={() => toggleSeat(seat)}
                        aria-label={`Row ${row} seat ${seat.seatNumber}, ${isSelected ? 'selected' : seat.status.toLowerCase()}`}
                        title={`${row}${seat.seatNumber} · ${formatPrice(seat.price)}`}
                      >
                        {seat.seatNumber}
                      </button>
                    )
                  })}
                  <span className="row-label">{row}</span>
                </div>
              ))}
            </div>
            <div className="seat-legend">
              <span><i className="legend-dot available-dot" /> Available</span>
              <span><i className="legend-dot selected-dot" /> Selected</span>
              <span><i className="legend-dot taken-dot" /> Taken</span>
            </div>
          </>
        )}
        {error && <p className="form-error" role="alert">{error}</p>}
        {!loading && (
          <div className="booking-footer">
            <div>
              <p className="selection-summary">{selected.length ? `${selected.length} seat${selected.length > 1 ? 's' : ''} selected` : 'Choose your seats'}</p>
              <strong>{formatPrice(total)}</strong>
            </div>
            <button className="primary-button" disabled={busy || selected.length === 0} onClick={bookSeats}>
              {busy ? 'Reserving…' : token ? 'Reserve seats' : 'Sign in to book'}
            </button>
          </div>
        )}
      </section>
    </div>
  )
}

function ConfirmationDialog({ booking, onClose }) {
  return (
    <div className="dialog-backdrop">
      <section className="dialog confirmation-dialog" role="dialog" aria-modal="true" aria-labelledby="confirmation-title">
        <div className="confirmation-icon" aria-hidden="true">✓</div>
        <p className="eyebrow">YOU’RE ON THE LIST</p>
        <h2 id="confirmation-title">Booking confirmed.</h2>
        <p className="dialog-intro">{booking.eventTitle} · Booking #{booking.bookingId}</p>
        <div className="ticket-details">
          <span>Tickets</span>
          <strong>{booking.ticketNumbers?.join(' · ') || 'Confirmed'}</strong>
          <span>Payment reference</span>
          <strong>{booking.paymentReference}</strong>
          <span>Total</span>
          <strong>{formatPrice(booking.totalAmount)}</strong>
        </div>
        <p className="payment-note">Payment is simulated for this project demo; no money was charged.</p>
        <button className="primary-button full-button" onClick={onClose}>Back to events</button>
      </section>
    </div>
  )
}

function App() {
  const [events, setEvents] = useState([])
  const [search, setSearch] = useState('')
  const [status, setStatus] = useState('loading')
  const [error, setError] = useState('')
  const [selectedEvent, setSelectedEvent] = useState(null)
  const [authOpen, setAuthOpen] = useState(false)
  const [bookingConfirmation, setBookingConfirmation] = useState(null)
  const [user, setUser] = useState(() => {
    try { return JSON.parse(localStorage.getItem(API_USER_KEY)) } catch { return null }
  })
  const [token, setToken] = useState(() => localStorage.getItem(API_TOKEN_KEY))

  useEffect(() => {
    let active = true
    apiRequest('/events')
      .then((result) => {
        if (active) {
          setEvents(Array.isArray(result) ? result : [])
          setStatus('ready')
        }
      })
      .catch((requestError) => {
        if (active) {
          setError(requestError.message)
          setStatus('error')
        }
      })
    return () => { active = false }
  }, [])

  const filteredEvents = useMemo(() => {
    const query = search.trim().toLowerCase()
    if (!query) return events
    return events.filter((event) => [
      event.title,
      event.description,
      event.venue?.name,
      event.venue?.city,
    ].some((value) => value?.toLowerCase().includes(query)))
  }, [events, search])

  function handleAuthenticated(auth) {
    setUser({ userId: auth.userId, fullName: auth.fullName, email: auth.email, role: auth.role })
    setToken(auth.token)
    localStorage.setItem(API_TOKEN_KEY, auth.token)
    localStorage.setItem(API_USER_KEY, JSON.stringify({
      userId: auth.userId,
      fullName: auth.fullName,
      email: auth.email,
      role: auth.role,
    }))
    setAuthOpen(false)
  }

  function signOut() {
    setUser(null)
    setToken(null)
    localStorage.removeItem(API_TOKEN_KEY)
    localStorage.removeItem(API_USER_KEY)
  }

  function handleBooking(booking) {
    setSelectedEvent(null)
    setBookingConfirmation(booking)
  }

  return (
    <div className="app-shell">
      <header className="site-header">
        <a className="brand" href="#" aria-label="VenueVista home">
          <span className="brand-mark">V</span>
          VenueVista
        </a>
        <nav className="main-nav" aria-label="Main navigation">
          <a href="#events">Discover</a>
          <a href="#about">The experience</a>
        </nav>
        <div className="header-account">
          {user ? (
            <>
              <span className="welcome-name">Hi, {user.fullName.split(' ')[0]}</span>
              <button className="outline-button" onClick={signOut}>Sign out</button>
            </>
          ) : (
            <button className="outline-button" onClick={() => setAuthOpen(true)}>Sign in</button>
          )}
        </div>
      </header>

      <main>
        <section className="hero-section">
          <div className="hero-content">
            <p className="eyebrow"><span className="live-dot" /> GOOD NIGHTS, BOOKED</p>
            <h1>Make room<br />for <em>the moment.</em></h1>
            <p className="hero-copy">The shows, sounds and stories worth getting out for. Find your next favorite night.</p>
            <a className="primary-button hero-cta" href="#events">Explore events <span aria-hidden="true">↓</span></a>
          </div>
          <div className="hero-art" aria-hidden="true">
            <div className="hero-sun" />
            <div className="hero-arch arch-outer" />
            <div className="hero-arch arch-inner" />
            <div className="hero-line line-one" />
            <div className="hero-line line-two" />
            <span className="hero-art-caption">LIVE<br />A LITTLE</span>
            <span className="hero-art-number">01 — 04</span>
          </div>
          <div className="hero-bottom">
            <span>Find a night that feels like yours</span>
            <span>Scroll to discover <b aria-hidden="true">↓</b></span>
          </div>
        </section>

        <section className="events-section" id="events">
          <div className="section-heading">
            <div>
              <p className="eyebrow">YOUR PLANS START HERE</p>
              <h2>Out there, <em>waiting.</em></h2>
            </div>
            <label className="search-box">
              <span aria-hidden="true">⌕</span>
              <input
                type="search"
                placeholder="Search events or places"
                value={search}
                onChange={(event) => setSearch(event.target.value)}
                aria-label="Search events or places"
              />
            </label>
          </div>

          {status === 'loading' && <p className="loading-message">Gathering the good stuff…</p>}
          {status === 'error' && (
            <div className="empty-state">
              <h3>We couldn’t reach the event list.</h3>
              <p>{error}. Check that the backend is running at localhost:8080, then refresh.</p>
            </div>
          )}
          {status === 'ready' && filteredEvents.length > 0 && (
            <div className="event-grid">
              {filteredEvents.map((event) => (
                <EventCard key={event.id} event={event} onSelect={setSelectedEvent} />
              ))}
            </div>
          )}
          {status === 'ready' && filteredEvents.length === 0 && (
            <div className="empty-state">
              <h3>{events.length ? 'No matching events.' : 'The calendar is taking a breath.'}</h3>
              <p>{events.length ? 'Try another search.' : 'Check back soon for something worth staying out for.'}</p>
            </div>
          )}
        </section>

        <section className="experience-section" id="about">
          <div className="experience-number">02 / 02</div>
          <p className="eyebrow">LESS SCROLLING. MORE SINGING.</p>
          <h2>From “we should go”<br />to <em>“that was unreal.”</em></h2>
          <p className="experience-copy">Pick an event, choose your seats, and lock in the night. Your next story starts with a ticket.</p>
          <a className="outline-button light-button" href="#events">Find your event <span aria-hidden="true">↗</span></a>
        </section>
      </main>

      <footer className="site-footer">
        <a className="brand footer-brand" href="#">VenueVista</a>
        <p>Made for nights worth remembering.</p>
        <span>VenueVista · Demo</span>
      </footer>

      {authOpen && (
        <AuthDialog onClose={() => setAuthOpen(false)} onAuthenticated={handleAuthenticated} />
      )}
      {selectedEvent && (
        <BookingDialog
          event={selectedEvent}
          token={token}
          onClose={() => setSelectedEvent(null)}
          onBooked={handleBooking}
          onSignIn={() => { setSelectedEvent(null); setAuthOpen(true) }}
        />
      )}
      {bookingConfirmation && (
        <ConfirmationDialog booking={bookingConfirmation} onClose={() => setBookingConfirmation(null)} />
      )}
    </div>
  )
}

export default App
