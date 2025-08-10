import { Link } from 'react-router-dom'
import {
  Box,
  Container,
  Grid,
  Typography,
  Link as MuiLink,
  Divider,
} from '@mui/material'

const Footer = () => {
  return (
    <Box
      component="footer"
      sx={{
        bgcolor: 'grey.900',
        color: 'white',
        py: 6,
        mt: 'auto',
      }}
    >
      <Container maxWidth="xl">
        <Grid container spacing={4}>
          <Grid item xs={12} md={3}>
            <Typography variant="h6" gutterBottom fontWeight={600}>
              Ecommerce
            </Typography>
            <Typography variant="body2" color="grey.300">
              Your trusted multi-tenant ecommerce platform for modern businesses.
            </Typography>
          </Grid>
          <Grid item xs={12} md={3}>
            <Typography variant="h6" gutterBottom fontWeight={600}>
              Quick Links
            </Typography>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
              <MuiLink component={Link} to="/" color="grey.300" underline="hover">
                Home
              </MuiLink>
              <MuiLink component={Link} to="/products" color="grey.300" underline="hover">
                Products
              </MuiLink>
              <MuiLink component={Link} to="/about" color="grey.300" underline="hover">
                About
              </MuiLink>
              <MuiLink component={Link} to="/contact" color="grey.300" underline="hover">
                Contact
              </MuiLink>
            </Box>
          </Grid>
          <Grid item xs={12} md={3}>
            <Typography variant="h6" gutterBottom fontWeight={600}>
              Support
            </Typography>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
              <MuiLink component={Link} to="/help" color="grey.300" underline="hover">
                Help Center
              </MuiLink>
              <MuiLink component={Link} to="/faq" color="grey.300" underline="hover">
                FAQ
              </MuiLink>
              <MuiLink component={Link} to="/shipping" color="grey.300" underline="hover">
                Shipping Info
              </MuiLink>
              <MuiLink component={Link} to="/returns" color="grey.300" underline="hover">
                Returns
              </MuiLink>
            </Box>
          </Grid>
          <Grid item xs={12} md={3}>
            <Typography variant="h6" gutterBottom fontWeight={600}>
              Legal
            </Typography>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
              <MuiLink component={Link} to="/privacy" color="grey.300" underline="hover">
                Privacy Policy
              </MuiLink>
              <MuiLink component={Link} to="/terms" color="grey.300" underline="hover">
                Terms of Service
              </MuiLink>
              <MuiLink component={Link} to="/cookies" color="grey.300" underline="hover">
                Cookie Policy
              </MuiLink>
            </Box>
          </Grid>
        </Grid>
        <Divider sx={{ my: 4, borderColor: 'grey.700' }} />
        <Typography
          variant="body2"
          color="grey.300"
          align="center"
        >
          © 2024 Ecommerce. All rights reserved.
        </Typography>
      </Container>
    </Box>
  )
}

export default Footer
