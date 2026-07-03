# Household Inventory Management System - Project Requirements

## Project Overview
An AI-powered household inventory management system with computer vision, voice recognition, and smart recommendations to help multiple household members track items, manage shopping lists, prevent waste, and ensure food safety.

---

## Decisions Made (2026-07-02)

The following previously-open questions are now decided. Sections below marked "(TO DECIDE)" that are covered here should be read as resolved.

### Technology Stack (DECIDED)
- **Platform**: Mobile-friendly **web application** (works on every phone/tablet/computer via the browser; no app-store friction). Native app can come later if camera UX demands it.
- **Frontend**: **Angular + Angular Material** (chosen over Next.js: developer already has years of Angular experience — shipping speed beats framework trendiness; SSR/SEO is irrelevant for a private household app).
- **Database / Auth / Real-time sync**: **Supabase** (Postgres + built-in auth + real-time subscriptions) via `supabase-js`, which is framework-agnostic. This single choice covers multi-user accounts, activity tracking ("who added what"), and live sync across devices.
- **Server-side logic**: **Supabase Edge Functions** — used in Phase 2 to call the Claude API so the API key never reaches the browser.
- **Hosting**: static Angular build on **Vercel / Firebase Hosting / Cloudflare Pages** (app) + **Supabase** (data + functions) — free tier at household scale. Budget: ~$0/month for Phase 1.
- **AI (vision, OCR, and conversational interface)**: **Claude API (multimodal)**, called from Supabase Edge Functions. One API call takes a product photo and returns name, brand, category, and the expiration date read off the label — this replaces the entire "Google Vision vs. Azure vs. custom model training" decision. Voice starts as browser speech-to-text (Web Speech API) feeding the same API.
- **No custom ML models.** Produce-freshness scoring and voice-based user identification are deferred indefinitely (worst effort-to-value ratio in this document).

### Product Decisions (DECIDED)
- **Product specificity**: Track at product-type + variant level ("2% Milk" ≠ "Whole Milk"); brand recorded as a field, not a separate product.
- **User identification**: Simple login per household member (Supabase Auth). No voice identification.
- **Categories**: Fixed default set (Dairy, Produce, Meat, Pantry, Frozen, Beverages, Cleaning, Toiletries, Pet, Medicine) + user-defined custom categories.
- **Recipe source (Phase 3)**: TheMealDB (free) to start; Spoonacular if outgrown.
- **Nutrition data (Phase 3)**: USDA FoodData Central (free), on-demand lookup only.
- **Recalls (Phase 3)**: openFDA API (free, US). Location: single region, set manually in settings.

---

## Development Roadmap (Phased)

**Guiding rule: ship Phase 1 before refining anything else in this document.**

### Phase 1 — Core MVP (build now, ~1–2 weeks)
- Manual item entry (name, category, quantity, expiration date)
- Item types vs. item instances (two milk cartons, different expiration dates)
- "Expiring soon" dashboard + expired-item flagging
- Shared shopping list (add manually; auto-add when an item runs out)
- Multi-user accounts with real-time sync and activity history
- Mobile-friendly UI

### Phase 2 — AI capture (after Phase 1 is deployed and used)
- Photo capture → Claude API identifies product + reads expiration date → user confirms → item added
- Voice input (browser speech-to-text → same natural-language pipeline)
- Interactive clarification UI (fixed-choice buttons / free text / image upload)

### Phase 3 — Integrations (only after real usage)
- Recipe suggestions prioritizing expiring items (TheMealDB)
- Nutrition lookup (USDA FoodData Central)
- Daily recall checks (openFDA)
- Dietary profiles & recipe filtering

### Deferred / Dropped
- Produce freshness assessment from photos (custom ML — dropped)
- Voice-based speaker identification (dropped)
- Smart-home / fridge-camera integration (deferred)
- Budget tracking, coupons, delivery integration (deferred)

---

## Core Objectives

1. **Track household inventory** - Know what items are currently in the house
2. **Identify missing items** - Maintain shopping list of items needed
3. **Monitor expiration dates** - Prevent waste and food safety issues
4. **Multi-user collaboration** - Enable household members to communicate and coordinate
5. **Smart recommendations** - Suggest recipes, usage priorities, and alerts

---

## Key Features

### 1. Image Recognition & Processing
- **Camera capture** for adding/removing items
- **Product identification** from packaging (product name, type, brand)
- **OCR for expiration dates** from packaging labels
- **Produce freshness assessment**:
  - Visual analysis: color, spots, bruising, texture
  - Ripeness detection for fruits/vegetables
  - Recommendations: "These bananas are ripe - eat in 2 days" vs "Still green - wait 3 days"
  - Different assessment rules per produce type

### 2. User Interface & Interaction

#### Voice Recognition
- **Full conversational AI interface** - natural language interactions
- **All system interactions available via voice**:
  - Add/remove items (with or without camera)
  - Query inventory ("What do we have?", "What's expiring?")
  - Update quantities
  - Manage shopping lists
  - Request recipes
  - Check specific items
- **Voice + Camera combo workflow**:
  - Hold up item to camera
  - System recognizes product
  - Voice confirms action: "Adding" or "Using it"
- **User asks clarifying questions** when needed
- **Safety confirmations** for bulk operations (e.g., clearing all items)

#### Interactive GUI Response System
- **Structured question/answer interface** when system needs user input
- **Response options include**:
  - **Fixed choice buttons** - Multiple predefined options to choose from
  - **Free text input** - Open-ended text response field
  - **Image upload with text** - Ability to upload image along with text description
- **Use cases**:
  - System asks: "What type of milk is this?" → Options: "Whole", "2%", "Skim", "Other (specify)"
  - System asks: "How many did you purchase?" → Free text number input
  - System asks: "Can you show me the expiration date?" → Image upload with optional text
  - Dietary preference selection during user profile setup
  - Category disambiguation when adding items
  - Recipe selection from multiple suggestions
- **Benefits**:
  - Faster interaction for common choices
  - Guides users with structured options
  - Reduces voice recognition errors
  - Better for visual information (expiration dates, labels)
  - Flexibility for edge cases via free text/image

### 3. Multi-User System
- **All household members can access and update**
- **Real-time synchronization** across devices
- **Activity tracking**: Who added/used what and when
- **User profiles** with individual preferences (see User Profiles section)

### 4. Item & Inventory Management

#### Item Instance Tracking
- **Item Type vs Item Instances**:
  - Item Type: "Milk" (general product)
  - Item Instances: Multiple containers with different expiration dates
- **Each instance tracks**:
  - Expiration date
  - Purchase/add date
  - Who added it
  - Quantity/size
  - Category

#### Product Specificity
- **Different products tracked separately**:
  - Regular milk ≠ Coconut milk
  - Organic eggs ≠ Regular eggs
  - Whole wheat bread ≠ White bread
  - Different brands can be tracked separately
- **Same product, multiple instances**:
  - 2 bottles of same milk brand with different expiration dates
  - System tracks separately but recognizes as same product type

#### Smart Usage Recommendations
- **Proactive alerts** (general recommendations):
  - "Your regular milk expires Jan 10 - use it soon!"
  - "You have 3 items expiring tomorrow"
  - Daily/periodic reminders about what needs to be used
- **Non-intrusive during active use**:
  - System does NOT interrupt when user is actively using an item
  - Trusts user's choice in the moment
  - User might have reasons for using longer-lasting item first

#### Product Specificity Level (TO DECIDE)
- How specific: Brand-level or just product type?
- Track "2% Milk" vs "Whole Milk" as different products?
- User preference learning over time?

### 5. Categorization System

#### Automatic Categorization
- **Items automatically labeled by category**:
  - Food categories: Dairy, Produce, Meat, Pantry/Dry Goods, Frozen, Beverages
  - Non-food: Cleaning Supplies, Toiletries, Pet Supplies, Medicine
- **Single unified system** with filtering/viewing by category
- **Category-specific behavior**:
  - Produce: freshness assessment via image
  - Packaged goods: OCR expiration dates
  - Cleaning supplies: may not need expiration tracking
  - Frozen items: longer shelf life calculations

#### Viewing & Organization
- Main view shows everything
- Filter by category
- Dashboard showing expiration alerts per category
- Shopping list auto-grouped by category

#### Category Customization (TO DECIDE)
- Should users be able to create custom categories?
- Essential default categories for household?

### 6. Expiration & Freshness Tracking

#### Items with Expiration Dates
- Track expiration for packaged goods
- Alert X days before expiration
- Prioritize "use soon" items
- Handle multiple instances with different expiration dates

#### Items without Expiration Dates
- Produce: estimate shelf life based on freshness assessment
- Leftovers: estimate based on item type and storage date
- Bulk items: user-defined or system-learned shelf life

#### Expired Items
- Auto-flag expired items
- Recommend removal or adding to shopping list
- Track waste (items that expired unused)

### 7. Shopping List Management

#### Adding to Shopping List
- Manually add items via voice/interface
- Auto-add when items run out
- Auto-add expired items (if user confirms)
- Add missing recipe ingredients

#### Shopping List Features
- Filter by store/section
- Check off items as purchased
- Track who purchased what
- Price tracking (optional)
- Budget limits (optional)

#### When at Store
- Help user navigate shopping
- Real-time recall checks before purchase
- Verify items against shopping list

### 8. Recipe Integration

#### Recipe Suggestions
- **Adaptive based on user request**:
  - Quick suggestions using current inventory
  - Full meal planning for the week
  - **System asks when uncertain**: "Quick ideas or full meal plan?"

#### Recipe Matching
- Match recipes using available ingredients
- Prioritize recipes using items expiring soon
- Show % of ingredients available
- Suggest with missing ingredients: "You have 80% - add eggs and parmesan?"

#### Missing Ingredients
- Auto-add to shopping list (with confirmation)
- Substitution suggestions

#### Recipe Filtering
- **User dietary restrictions** (see User Profiles)
- **Always ask**: "Who are you cooking for?"
  - Individual household members
  - "Everyone" (only recipes safe for all)
  - Filters based on that person's/group's restrictions
- Time to cook, difficulty
- Dietary preferences, allergies

#### Recipe Sources (TO DECIDE)
- Online recipe APIs/databases (Spoonacular, TheMealDB)?
- User-saved family recipes?
- Learning system for frequently made recipes?

### 9. User Profiles & Dietary Management

#### Each User Profile Contains
- **Dietary restrictions**:
  - Allergies (nuts, dairy, shellfish, gluten, etc.)
  - Dietary choices (vegetarian, vegan, pescatarian, kosher, halal)
  - Health restrictions (low-sodium, diabetic-friendly, low-carb)
  - Preferences/dislikes
- **Calorie/nutritional goals** (see Nutrition Tracking)
- **Health conditions** requiring specific diets

#### Recipe Filtering Logic
- Hard filters: NEVER suggest recipes with allergens
- Soft filters: prefer certain diets but show others
- When cooking for "Everyone": only suggest recipes satisfying ALL restrictions

#### User Identification (TO DECIDE)
- Voice recognition to identify who's speaking?
- Manual selection: "Who's using the system?"
- Individual logins?

### 10. Nutritional Tracking & Health Data

#### Online Integration
- **Fetch nutritional data** from online databases:
  - USDA FoodData Central
  - Nutritionix, Edamam, or similar APIs
- **When recognized**: calories, protein, fat, carbs, sodium, etc.
- **Per-serving or per-package** tracking

#### Recipe Nutritional Info
- Show calories, macros, sodium for suggested recipes
- Filter recipes by nutritional criteria: "Under 500 calories", "High protein"
- Flag recipes violating health restrictions

#### Health Metrics (TO DECIDE)
- Automatically fetch nutrition data for every item?
- Only lookup when user asks?
- Track daily/weekly nutritional intake per person?
- Most important metrics for household?

### 11. Food Safety & Recall Monitoring

#### Daily Recall Checks
- **Automatic daily monitoring** of existing inventory
- **Location-aware**: Match recalls to user's geographic area
- **Date-specific**: Match purchase/add dates to recall date ranges
- **Product matching**: By product name, brand, and ideally UPC/barcode

#### Preventive Checks
- **Real-time checks** when adding new items (at store)
- Alert before purchase: "This product is currently under recall - don't buy!"

#### Recall Sources
- Government sources: FDA recalls, USDA alerts, CDC warnings
- Contamination: Salmonella, E. coli, Listeria
- Product recalls: contamination, mislabeling, allergens, foreign objects
- Regional/local health department warnings

#### Alert System
- **Urgency levels**:
  - Critical: "URGENT: Discard immediately!"
  - Moderate: Warning with recommended action
  - Minor: Notice/informational
- **Alert delivery**:
  - Immediate push notifications
  - Flag affected items in inventory
  - Link to official recall details
- **Recommended actions**: Discard, return for refund, etc.

#### Implementation Details (TO DECIDE)
- **Location granularity**: Country? State? County? Zip code?
- **Store tracking**: Record which store items came from?
- **Historical checks**: How far back when adding old items?
- **Alert timing**: Immediate vs morning summary?
- **Who gets notified**: All users or admin?

#### User Location (TO DECIDE)
- What is your location (country/region)?
- Single location or multiple (vacation home)?
- Auto-detect or manual setting?

---

## Technical Considerations

### Platform & Technology Stack (TO DECIDE)

#### Platform Options
- **Web application** (accessible from phones, tablets, computers)
- **Mobile app** (iOS/Android - better camera access)
- **Hybrid** (mobile-friendly web + native features)

#### Key Technologies Needed
- **Computer Vision & OCR**:
  - Pre-trained models or custom training?
  - Cloud AI services (Google Vision API, Azure, AWS) vs self-hosted
  - Cost per image vs hardware requirements
- **Voice Recognition & NLP**:
  - Speech-to-text services
  - Conversational AI (intent recognition, context management)
  - User identification via voice?
- **Interactive GUI Framework**:
  - Frontend framework supporting dynamic forms and interactive elements
  - File upload capabilities (image handling)
  - Real-time validation and response handling
  - Mobile-responsive design for touch interactions
  - Component libraries for buttons, inputs, image upload widgets
- **Image Analysis for Produce**:
  - Freshness detection models (color analysis, defect detection)
  - Custom training required for different produce types
- **Backend & Database**:
  - Real-time sync for multi-user
  - Handle image storage
  - API integrations
- **APIs & Services**:
  - Nutrition databases
  - Recipe APIs
  - Recall monitoring services
  - Barcode/UPC databases

#### User Technical Background (TO DECIDE)
- Do you code? What languages?
- Comfortable with web/mobile development?
- Experience with AI/ML?
- Prefer frameworks/templates or build from scratch?

#### Hosting (TO DECIDE)
- Self-hosted (home network/server)?
- Cloud-hosted (accessible anywhere)?
- Hybrid (local-first with cloud sync)?

#### Budget Considerations
- Cloud AI APIs cost money per API call
- Self-hosted models need good hardware
- Ongoing costs for hosting, APIs, storage

### Development Approach (TO DECIDE)

#### MVP vs Full Vision
- Start simple (manual entry + basic tracking) then add AI?
- Or dive into camera/voice features from start?
- Phased rollout of features?

#### Technology Preferences
- Existing tech preferences or recommendations needed?
- Python, JavaScript, React, Vue, Flutter, React Native?
- Existing frameworks to leverage?

---

## Open Questions & Decisions Needed

### High Priority
1. **Your location/region** - Determines relevant recall sources and APIs
2. **Technical background** - Helps determine appropriate tech stack
3. **Platform preference** - Web app vs mobile app vs both
4. **MVP scope** - Which features to build first vs later phases
5. **Budget considerations** - Cloud services vs self-hosted

### Medium Priority
6. **Product specificity level** - How detailed should product matching be?
7. **Essential categories** - What categories matter most for your household?
8. **User identification method** - Voice recognition, login, or manual selection?
9. **Nutrition tracking depth** - Automatic for all items or on-demand?
10. **Recipe sources** - Which APIs/services to use?

### Lower Priority
11. **Custom categories** - Should users create their own?
12. **Historical recall checks** - How far back to check?
13. **Meal tracking** - Track what each person ate, or just inventory?
14. **Store tracking** - Record purchase location per item?
15. **Guest/visitor support** - Temporary dietary restrictions?

---

## Additional Features to Consider

### Potentially Useful
- Budget tracking (spending on groceries)
- Store preferences (where to buy what)
- Coupon/sales integration
- Purchase history and patterns (how often you buy milk)
- Waste tracking analytics
- Consumption patterns per household member
- Recurring items (auto-suggest when to rebuy)
- Integration with online grocery delivery

### Smart Home Integration
- Fridge cameras
- Pantry sensors (weight, inventory)
- Smart speakers for voice
- Automated alerts/notifications

### Advanced Features
- Meal planning calendar
- Household task coordination (beyond inventory)
- Learning system (preferences, patterns, habits)
- Social features (share recipes with other households)

---

## Next Steps

> Superseded by **Development Roadmap (Phased)** at the top of this document.

1. ~~Answer open questions~~ — high-priority questions resolved in **Decisions Made (2026-07-02)**
2. ~~Define MVP scope~~ — defined as Phase 1
3. ~~Choose technology stack~~ — Angular + Supabase (+ Edge Functions) + Claude API
4. **Design Phase 1 database schema** (item types, item instances, users, shopping list)
5. **Scaffold the Angular + Supabase app and deploy an empty shell**
6. **Build Phase 1 features and start using them daily**

---

## Notes

- System philosophy: **Ask users when uncertain, don't assume**
- Design principle: **Helpful, not presumptuous**
- User experience: **Trust user choices in the moment, provide proactive recommendations separately**
- Safety: **Protect against bulk deletions, contaminated products, allergens**
- Collaboration: **Enable household coordination and communication**

---

**Document created**: 2026-01-04
**Status**: Decisions made — ready to build Phase 1
**Last updated**: 2026-07-02
