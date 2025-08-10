import { Link, useNavigate } from 'react-router-dom'
import { useForm, Controller } from 'react-hook-form'
import { useMutation } from '@tanstack/react-query'
import {
  Box,
  Paper,
  TextField,
  Button,
  Typography,
  Link as MuiLink,
  Alert,
  CircularProgress,
} from '@mui/material'
import { authService, LoginRequest } from '../services/authService'
import { useAuthStore } from '../stores/authStore'
import { useSnackbar } from 'notistack'

const Login = () => {
  const navigate = useNavigate()
  const { login } = useAuthStore()
  const { enqueueSnackbar } = useSnackbar()

  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginRequest>()

  const loginMutation = useMutation({
    mutationFn: authService.login,
    onSuccess: (data) => {
      login(data.user, data.token)
      enqueueSnackbar('Login successful!', { variant: 'success' })
      navigate('/dashboard')
    },
    onError: (error: any) => {
      enqueueSnackbar(error.response?.data?.message || 'Login failed', { variant: 'error' })
    },
  })

  const onSubmit = (data: LoginRequest) => {
    loginMutation.mutate(data)
  }

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        bgcolor: 'background.default',
        py: 8,
        px: 2,
      }}
    >
      <Paper
        elevation={3}
        sx={{
          p: 4,
          width: '100%',
          maxWidth: 400,
          borderRadius: 2,
        }}
      >
        <Box sx={{ textAlign: 'center', mb: 4 }}>
          <Typography variant="h4" component="h1" gutterBottom fontWeight={700}>
            Sign in to your account
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Or{' '}
            <MuiLink component={Link} to="/register" underline="hover">
              create a new account
            </MuiLink>
          </Typography>
        </Box>

        <Box component="form" onSubmit={handleSubmit(onSubmit)} sx={{ space: 3 }}>
          <Controller
            name="email"
            control={control}
            defaultValue=""
            rules={{
              required: 'Email is required',
              pattern: {
                value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                message: 'Invalid email address',
              },
            }}
            render={({ field }) => (
              <TextField
                {...field}
                fullWidth
                label="Email address"
                type="email"
                margin="normal"
                error={!!errors.email}
                helperText={errors.email?.message}
                placeholder="Enter your email"
              />
            )}
          />

          <Controller
            name="password"
            control={control}
            defaultValue=""
            rules={{
              required: 'Password is required',
              minLength: {
                value: 6,
                message: 'Password must be at least 6 characters',
              },
            }}
            render={({ field }) => (
              <TextField
                {...field}
                fullWidth
                label="Password"
                type="password"
                margin="normal"
                error={!!errors.password}
                helperText={errors.password?.message}
                placeholder="Enter your password"
              />
            )}
          />

          {loginMutation.isError && (
            <Alert severity="error" sx={{ mt: 2 }}>
              {loginMutation.error?.response?.data?.message || 'Login failed'}
            </Alert>
          )}

          <Button
            type="submit"
            fullWidth
            variant="contained"
            size="large"
            disabled={loginMutation.isLoading}
            sx={{ mt: 3, mb: 2, py: 1.5 }}
          >
            {loginMutation.isLoading ? (
              <>
                <CircularProgress size={20} sx={{ mr: 1 }} />
                Signing in...
              </>
            ) : (
              'Sign in'
            )}
          </Button>

          <Box sx={{ textAlign: 'center', mt: 2 }}>
            <MuiLink component={Link} to="/forgot-password" variant="body2" underline="hover">
              Forgot your password?
            </MuiLink>
          </Box>
        </Box>
      </Paper>
    </Box>
  )
}

export default Login
