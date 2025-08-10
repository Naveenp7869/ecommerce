import { Outlet } from 'react-router-dom'
import { Box, Container } from '@mui/material'
import Header from './Header'
import Footer from './Footer'

const Layout = () => {
  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      <Header />
      <Box component="main" sx={{ flex: 1, py: 3 }}>
        <Container maxWidth="xl">
          <Outlet />
        </Container>
      </Box>
      <Footer />
    </Box>
  )
}

export default Layout
