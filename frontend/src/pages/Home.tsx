import { Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import {
  Box,
  Container,
  Typography,
  Button,
  Grid,
  Card,
  CardContent,
  Avatar,
  Stack,
} from '@mui/material'
import {
  Store,
  Security,
  LocalShipping,
} from '@mui/icons-material'
import { productService } from '../services/productService'
import ProductCard from '../components/ProductCard'

const Home = () => {
  const { data: products = [] } = useQuery({
    queryKey: ['products', 'featured'],
    queryFn: () => productService.getProducts({ search: '', limit: 6 }),
  })

  const features = [
    {
      icon: <Store sx={{ fontSize: 40 }} />,
      title: 'Multi-Vendor Platform',
      description: 'Shop from multiple vendors in one place with seamless experience.',
    },
    {
      icon: <Security sx={{ fontSize: 40 }} />,
      title: 'Secure Shopping',
      description: 'Your data is protected with industry-standard security measures.',
    },
    {
      icon: <LocalShipping sx={{ fontSize: 40 }} />,
      title: 'Fast Delivery',
      description: 'Quick and reliable delivery to your doorstep.',
    },
  ]

  return (
    <Box>
      {/* Hero Section */}
      <Box
        sx={{
          background: 'linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%)',
          color: 'white',
          py: { xs: 8, md: 12 },
          textAlign: 'center',
        }}
      >
        <Container maxWidth="lg">
          <Typography
            variant="h2"
            component="h1"
            gutterBottom
            sx={{
              fontWeight: 700,
              fontSize: { xs: '2.5rem', md: '4rem' },
              mb: 3,
            }}
          >
            Welcome to Multi-Tenant Ecommerce
          </Typography>
          <Typography
            variant="h6"
            sx={{
              mb: 4,
              maxWidth: 600,
              mx: 'auto',
              opacity: 0.9,
              lineHeight: 1.6,
            }}
          >
            Discover amazing products from multiple vendors in one place.
            Shop with confidence and enjoy seamless shopping experience.
          </Typography>
          <Stack
            direction={{ xs: 'column', sm: 'row' }}
            spacing={2}
            justifyContent="center"
            sx={{ mt: 4 }}
          >
            <Button
              component={Link}
              to="/products"
              variant="contained"
              size="large"
              sx={{
                bgcolor: 'white',
                color: 'primary.main',
                px: 4,
                py: 1.5,
                '&:hover': {
                  bgcolor: 'grey.100',
                },
              }}
            >
              Browse Products
            </Button>
            <Button
              component={Link}
              to="/register"
              variant="outlined"
              size="large"
              sx={{
                borderColor: 'white',
                color: 'white',
                px: 4,
                py: 1.5,
                '&:hover': {
                  borderColor: 'white',
                  bgcolor: 'white',
                  color: 'primary.main',
                },
              }}
            >
              Join Now
            </Button>
          </Stack>
        </Container>
      </Box>

      {/* Features Section */}
      <Box sx={{ py: { xs: 6, md: 10 }, bgcolor: 'background.paper' }}>
        <Container maxWidth="lg">
          <Typography
            variant="h3"
            component="h2"
            align="center"
            gutterBottom
            sx={{ mb: 6, fontWeight: 700 }}
          >
            Why Choose Us
          </Typography>
          <Grid container spacing={4}>
            {features.map((feature, index) => (
              <Grid item xs={12} md={4} key={index}>
                <Box sx={{ textAlign: 'center' }}>
                  <Avatar
                    sx={{
                      bgcolor: 'primary.100',
                      color: 'primary.main',
                      width: 80,
                      height: 80,
                      mx: 'auto',
                      mb: 3,
                    }}
                  >
                    {feature.icon}
                  </Avatar>
                  <Typography variant="h5" component="h3" gutterBottom fontWeight={600}>
                    {feature.title}
                  </Typography>
                  <Typography variant="body1" color="text.secondary">
                    {feature.description}
                  </Typography>
                </Box>
              </Grid>
            ))}
          </Grid>
        </Container>
      </Box>

      {/* Featured Products */}
      <Box sx={{ py: { xs: 6, md: 10 }, bgcolor: 'background.default' }}>
        <Container maxWidth="lg">
          <Typography
            variant="h3"
            component="h2"
            align="center"
            gutterBottom
            sx={{ mb: 6, fontWeight: 700 }}
          >
            Featured Products
          </Typography>
          <Grid container spacing={3}>
            {products.map((product) => (
              <Grid item xs={12} sm={6} md={4} key={product.id}>
                <ProductCard product={product} />
              </Grid>
            ))}
          </Grid>
          <Box sx={{ textAlign: 'center', mt: 6 }}>
            <Button
              component={Link}
              to="/products"
              variant="contained"
              size="large"
              sx={{ px: 4, py: 1.5 }}
            >
              View All Products
            </Button>
          </Box>
        </Container>
      </Box>
    </Box>
  )
}

export default Home
