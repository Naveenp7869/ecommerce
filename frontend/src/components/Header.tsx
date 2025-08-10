import React, { useState, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  IconButton,
  Badge,
  Menu,
  MenuItem,
  Avatar,
  Box,
  Divider,
  ListItemIcon,
  ListItemText,
} from '@mui/material'
import {
  ShoppingCart,
  Person,
  Dashboard,
  AdminPanelSettings,
  Logout,
  Notifications,
  Settings,
} from '@mui/icons-material'
import { useAuthStore } from '../stores/authStore'
import { useCartStore } from '../stores/cartStore'
import { useNotificationStore } from '../stores/notificationStore'
import kafkaService from '../services/kafkaService'

const Header = () => {
  const navigate = useNavigate()
  const { user, isAuthenticated, logout } = useAuthStore()
  const { getItemCount } = useCartStore()
  const { unreadCount, addNotification } = useNotificationStore()
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null)
  const [notificationAnchorEl, setNotificationAnchorEl] = useState<null | HTMLElement>(null)

  useEffect(() => {
    if (isAuthenticated && user) {
      // Subscribe to notifications
      const unsubscribe = kafkaService.subscribeToNotifications(
        user.id,
        (notification) => {
          addNotification(notification)
        }
      )

      return () => {
        unsubscribe()
      }
    }
  }, [isAuthenticated, user, addNotification])

  const handleMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget)
  }

  const handleMenuClose = () => {
    setAnchorEl(null)
  }

  const handleNotificationMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setNotificationAnchorEl(event.currentTarget)
  }

  const handleNotificationMenuClose = () => {
    setNotificationAnchorEl(null)
  }

  const handleLogout = () => {
    logout()
    handleMenuClose()
    navigate('/')
  }

  return (
    <AppBar position="sticky" elevation={1}>
      <Toolbar>
        {/* Logo */}
        <Typography
          variant="h6"
          component={Link}
          to="/"
          sx={{
            flexGrow: 0,
            mr: 4,
            textDecoration: 'none',
            color: 'inherit',
            fontWeight: 700,
          }}
        >
          Ecommerce
        </Typography>

        {/* Navigation */}
        <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' } }}>
          <Button color="inherit" component={Link} to="/">
            Home
          </Button>
          <Button color="inherit" component={Link} to="/products">
            Products
          </Button>
          {isAuthenticated && (
            <>
              <Button color="inherit" component={Link} to="/dashboard">
                Dashboard
              </Button>
              {user?.role === 'SUPER_ADMIN' && (
                <Button color="inherit" component={Link} to="/admin">
                  Admin
                </Button>
              )}
            </>
          )}
        </Box>

        {/* Right side actions */}
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          {/* Cart */}
          <IconButton color="inherit" component={Link} to="/cart">
            <Badge badgeContent={getItemCount()} color="error">
              <ShoppingCart />
            </Badge>
          </IconButton>

          {/* Notifications */}
          {isAuthenticated && (
            <IconButton color="inherit" onClick={handleNotificationMenuOpen}>
              <Badge badgeContent={unreadCount} color="error">
                <Notifications />
              </Badge>
            </IconButton>
          )}

          {/* User menu */}
          {isAuthenticated ? (
            <>
              <IconButton onClick={handleMenuOpen} sx={{ p: 0, ml: 1 }}>
                <Avatar sx={{ width: 32, height: 32 }}>
                  {user?.firstName?.charAt(0)}
                </Avatar>
              </IconButton>
              <Menu
                anchorEl={anchorEl}
                open={Boolean(anchorEl)}
                onClose={handleMenuClose}
                transformOrigin={{ horizontal: 'right', vertical: 'top' }}
                anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
              >
                <MenuItem component={Link} to="/profile" onClick={handleMenuClose}>
                  <ListItemIcon>
                    <Person fontSize="small" />
                  </ListItemIcon>
                  <ListItemText>Profile</ListItemText>
                </MenuItem>
                <MenuItem component={Link} to="/orders" onClick={handleMenuClose}>
                  <ListItemIcon>
                    <Dashboard fontSize="small" />
                  </ListItemIcon>
                  <ListItemText>Orders</ListItemText>
                </MenuItem>
                <MenuItem component={Link} to="/settings" onClick={handleMenuClose}>
                  <ListItemIcon>
                    <Settings fontSize="small" />
                  </ListItemIcon>
                  <ListItemText>Settings</ListItemText>
                </MenuItem>
                <Divider />
                <MenuItem onClick={handleLogout}>
                  <ListItemIcon>
                    <Logout fontSize="small" />
                  </ListItemIcon>
                  <ListItemText>Logout</ListItemText>
                </MenuItem>
              </Menu>
            </>
          ) : (
            <Box sx={{ display: 'flex', gap: 1 }}>
              <Button color="inherit" component={Link} to="/login">
                Login
              </Button>
              <Button
                variant="outlined"
                color="inherit"
                component={Link}
                to="/register"
                sx={{ borderColor: 'white', '&:hover': { borderColor: 'white' } }}
              >
                Register
              </Button>
            </Box>
          )}
        </Box>

        {/* Notification Menu */}
        <Menu
          anchorEl={notificationAnchorEl}
          open={Boolean(notificationAnchorEl)}
          onClose={handleNotificationMenuClose}
          transformOrigin={{ horizontal: 'right', vertical: 'top' }}
          anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
          PaperProps={{
            sx: { width: 350, maxHeight: 400 }
          }}
        >
          <MenuItem disabled>
            <Typography variant="subtitle1" fontWeight={600}>
              Notifications
            </Typography>
          </MenuItem>
          <Divider />
          {unreadCount === 0 ? (
            <MenuItem disabled>
              <Typography variant="body2" color="text.secondary">
                No new notifications
              </Typography>
            </MenuItem>
          ) : (
            <MenuItem component={Link} to="/notifications" onClick={handleNotificationMenuClose}>
              <Typography variant="body2">
                View all notifications ({unreadCount})
              </Typography>
            </MenuItem>
          )}
        </Menu>
      </Toolbar>
    </AppBar>
  )
}

export default Header
