---
name: BrickVault
colors:
  surface: '#f9f9f9'
  surface-dim: '#dadada'
  surface-bright: '#f9f9f9'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f3f3'
  surface-container: '#eeeeee'
  surface-container-high: '#e8e8e8'
  surface-container-highest: '#e2e2e2'
  on-surface: '#1a1c1c'
  on-surface-variant: '#5d3f3c'
  inverse-surface: '#2f3131'
  inverse-on-surface: '#f0f1f1'
  outline: '#926e6b'
  outline-variant: '#e7bdb8'
  surface-tint: '#c00017'
  primary: '#a30012'
  on-primary: '#ffffff'
  primary-container: '#d0021b'
  on-primary-container: '#ffdfdc'
  inverse-primary: '#ffb3ac'
  secondary: '#175ead'
  on-secondary: '#ffffff'
  secondary-container: '#72aafe'
  on-secondary-container: '#003d79'
  tertiary: '#745b00'
  on-tertiary: '#ffffff'
  tertiary-container: '#d0a600'
  on-tertiary-container: '#4f3d00'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffdad6'
  primary-fixed-dim: '#ffb3ac'
  on-primary-fixed: '#410003'
  on-primary-fixed-variant: '#93000f'
  secondary-fixed: '#d5e3ff'
  secondary-fixed-dim: '#a8c8ff'
  on-secondary-fixed: '#001b3c'
  on-secondary-fixed-variant: '#004689'
  tertiary-fixed: '#ffe08b'
  tertiary-fixed-dim: '#f1c100'
  on-tertiary-fixed: '#241a00'
  on-tertiary-fixed-variant: '#584400'
  background: '#f9f9f9'
  on-background: '#1a1c1c'
  surface-variant: '#e2e2e2'
typography:
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
    letterSpacing: -0.01em
  title-md:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.05em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  unit: 4px
  margin-mobile: 16px
  gutter: 12px
  stack-sm: 8px
  stack-md: 16px
  stack-lg: 24px
---

## Brand & Style
The design system for this product is a sophisticated interpretation of Material 3 principles, tailored specifically for adult enthusiasts who treat LEGO as both a hobby and an investment. The aesthetic avoids the "toy store" cliché, instead opting for a clean, precision-engineered look that mirrors the technical complexity of modern sets.

The style is **Corporate / Modern** with a high-utility focus. It utilizes a refined white-space strategy to ensure that the vibrant colors of the LEGO sets themselves remain the focal point. The interface feels like a high-end inventory management tool—reliable, archival, and structured—while retaining a "light playful personality" through the iconic primary color palette used in precise, intentional hits.

## Colors
The palette is anchored by a neutral #FAFAFA background to provide a gallery-like setting for photography. 

- **Primary Red (#D0021B):** Used for critical actions, FABs (Floating Action Buttons), and brand moments.
- **Secondary Blue (#0055A4):** Utilized for secondary navigation elements and links.
- **Tertiary Yellow (#F5C400):** Reserved for "In Progress" status indicators and highlighting rare attributes.
- **Quaternary Green (#00852B):** Used exclusively for positive status indicators like "Sealed" or "Complete."

Neutral tones follow a strict grayscale to maintain a professional, data-heavy environment without color bleed.

## Typography
This design system employs **Inter** across all levels to ensure maximum legibility and a systematic, utilitarian feel. The hierarchy is driven by weight and capitalization rather than excessive size shifts.

- **Headlines:** Use Bold/700 weight with tight letter spacing to feel impactful and structural.
- **Titles:** Use Semi-Bold/600 to clearly delineate sections within set details or inventory lists.
- **Labels:** Small labels for status badges and metadata utilize a slight tracking increase and uppercase transform to mimic technical serial numbers found on packaging.

## Layout & Spacing
The layout follows a **4px grid system** typical of Android development. For mobile, a standard 2-column or 1-column layout is used depending on the density of information.

- **Margins:** 16px horizontal margins on all mobile screens.
- **Card Spacing:** 12px gutters between cards in a grid view to maintain a tight, organized collection feel.
- **Touch Targets:** Minimum 48x48px for all interactive elements, despite the clean/minimal aesthetic.
- **Padding:** Internal card padding is set to 16px to ensure content does not feel cramped against the rounded corners.

## Elevation & Depth
Depth is handled through **Tonal Layers** rather than heavy shadows, staying true to Material 3.

- **Level 0 (Surface):** #FAFAFA for the main app background.
- **Level 1 (Cards):** #FFFFFF with a 1px subtle stroke (#E0E0E0) and a very soft, diffused shadow (Y: 2, Blur: 4, Opacity: 0.05).
- **Level 2 (Active/FAB):** Primary Red with a higher elevation shadow (Y: 4, Blur: 8, Opacity: 0.15) to indicate interactability.

Avoid high-contrast drop shadows. Depth should feel "paper-thin" and architectural.

## Shapes
The shape language is defined by a **Rounded (0.5rem / 8px - 16px)** approach. 

- **Standard Components:** Buttons and input fields use an 8px radius.
- **Containers:** Inventory cards and bottom sheets use a 16px radius for a friendlier, modern mobile feel.
- **Status Badges:** Use a fully "Pill-shaped" (capsule) geometry to distinguish them from functional buttons.

## Components

### Buttons
- **Primary:** Solid Primary Red, White text, 8px radius.
- **Secondary:** Transparent background, 1px Primary Red border, Primary Red text.
- **Tertiary:** No border, Blue text, for low-priority actions.

### Status Badges
- **Sealed:** Quaternary Green background (15% opacity), Quaternary Green text, Uppercase Label-sm.
- **In Progress:** Tertiary Yellow background (20% opacity), Dark Amber/Neutral-900 text.
- **Unbuilt:** Neutral Gray background, Dark Gray text.

### Cards
- **Inventory Card:** Fixed aspect ratio image (4:3) at the top, followed by Title-md and a horizontal row for price/piece-count metadata.
- **Interactive States:** Cards should show a subtle gray ripple effect on tap.

### Input Fields
- Outlined style with a 1px border (#E0E0E0). On focus, the border thickens to 2px and changes to Secondary Blue. Labels should be small and float above the input.

### List Items
- Clean, single-line or two-line list items with a 56px square thumbnail on the leading edge and a chevron on the trailing edge.