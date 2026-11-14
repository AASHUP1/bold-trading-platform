import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import { LoginForm } from './LoginForm';
import { AuthProvider } from '../hooks/useAuth';

describe('LoginForm Component', () => {
  it('renders login form with inputs and submit button', () => {
    render(
      <MemoryRouter>
        <AuthProvider>
          <LoginForm />
        </AuthProvider>
      </MemoryRouter>
    );

    expect(screen.getByText(/BOLD/i)).toBeInTheDocument();
    expect(screen.getByText(/CONTROL ROOM/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/admin@bold.app/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Authenticate & Access/i })).toBeInTheDocument();
  });

  it('allows typing email and password and shows quick role switcher', () => {
    render(
      <MemoryRouter>
        <AuthProvider>
          <LoginForm />
        </AuthProvider>
      </MemoryRouter>
    );

    const emailInput = screen.getByPlaceholderText(/admin@bold.app/i);
    fireEvent.change(emailInput, { target: { value: 'compliance.lead@boldtrading.internal' } });
    expect(emailInput).toHaveValue('compliance.lead@boldtrading.internal');

    const opsButton = screen.getByRole('button', { name: /Ops Admin/i });
    fireEvent.click(opsButton);
    expect(emailInput).toHaveValue('ops.admin@boldtrading.internal');
  });

  it('submits form and authenticates successfully', async () => {
    render(
      <MemoryRouter>
        <AuthProvider>
          <LoginForm />
        </AuthProvider>
      </MemoryRouter>
    );

    const submitBtn = screen.getByRole('button', { name: /Authenticate & Access/i });
    fireEvent.click(submitBtn);

    await waitFor(() => {
      // Session saved in localStorage
      expect(localStorage.getItem('bold_admin_session')).toBeTruthy();
    });
  });
});
