using System;
using System.Collections.Generic;

namespace Verse
{
    public class Def { public string defName; }
    public class ThingDef : Def { }
    public class HediffDef : Def { }
    public class RecipeDef : Def { }
    public struct IntVec3 { }
    public enum DestroyMode { Vanish }
    public enum ThingPlaceMode { Near }

    public class Map
    {
        public List<MapComponent> components = new List<MapComponent>();
        public ListerBuildings listerBuildings = new ListerBuildings();
        public MapPawns mapPawns = new MapPawns();
        public T GetComponent<T>() where T : MapComponent
        {
            for (int i = 0; i < components.Count; i++) if (components[i] is T) return (T)components[i];
            return null;
        }
    }
    public class ListerBuildings { public List<Building> allBuildingsColonist = new List<Building>(); }
    public class MapPawns { public List<Pawn> AllPawnsSpawned = new List<Pawn>(); }
    public class MapComponent
    {
        protected Map map;
        public MapComponent(Map map) { this.map = map; }
        public virtual void FinalizeInit() { }
        public virtual void ExposeData() { }
        public virtual void MapComponentTick() { }
    }

    public class Thing
    {
        public int thingIDNumber;
        public bool Spawned;
        public bool Destroyed;
        public Map Map;
        public IntVec3 Position;
        public ThingDef def;
        public int stackCount;
        public RimWorld.Faction Faction;
        public string LabelCap;
        public virtual bool IsHashIntervalTick(int interval) { return false; }
    }
    public class ThingWithComps : Thing
    {
        public List<ThingComp> AllComps = new List<ThingComp>();
        public T GetComp<T>() where T : ThingComp
        {
            for (int i = 0; i < AllComps.Count; i++) if (AllComps[i] is T) return (T)AllComps[i];
            return null;
        }
    }
    public class Building : ThingWithComps
    {
        public virtual void SpawnSetup(Map map, bool respawningAfterLoad) { Map = map; Spawned = true; }
        public virtual void DeSpawn(DestroyMode mode = DestroyMode.Vanish) { Spawned = false; }
        public virtual string GetInspectString() { return string.Empty; }
        public virtual IEnumerable<Gizmo> GetGizmos() { yield break; }
    }
    public class Pawn : ThingWithComps
    {
        public Pawn_HealthTracker health = new Pawn_HealthTracker();
    }
    public class Pawn_HealthTracker
    {
        public HediffSet hediffSet = new HediffSet();
        public void AddHediff(Hediff h) { if (h != null) hediffSet.hediffs.Add(h); }
        public void RemoveHediff(Hediff h) { hediffSet.hediffs.Remove(h); }
    }
    public class HediffSet
    {
        public List<Hediff> hediffs = new List<Hediff>();
        public Hediff GetFirstHediffOfDef(HediffDef def)
        {
            for (int i = 0; i < hediffs.Count; i++) if (hediffs[i].def == def) return hediffs[i];
            return null;
        }
    }
    public class Hediff { public HediffDef def; public float Severity; }
    public class Hediff_Injury : Hediff { public void Heal(float amount) { Severity -= amount; } }
    public static class HediffMaker { public static Hediff MakeHediff(HediffDef def, Pawn pawn) { return new Hediff { def = def }; } }

    public class CompProperties { public Type compClass; }
    public class ThingComp
    {
        public ThingWithComps parent;
        public CompProperties props;
        public virtual void PostExposeData() { }
        public virtual void PostSpawnSetup(bool respawningAfterLoad) { }
        public virtual void CompTick() { }
    }

    public class Gizmo { }
    public class Command_Action : Gizmo
    {
        public string defaultLabel;
        public string defaultDesc;
        public Action action;
        public void Disable(string reason) { }
    }

    public static class DefDatabase<T> where T : Def { public static T GetNamedSilentFail(string name) { return null; } }
    public static class ThingMaker { public static Thing MakeThing(ThingDef def) { return new Thing { def = def }; } }
    public static class GenPlace { public static bool TryPlaceThing(Thing thing, IntVec3 loc, Map map, ThingPlaceMode mode) { return true; } }
    public static class Scribe_Values
    {
        public static void Look(ref int value, string label, int defaultValue) { }
        public static void Look(ref float value, string label, float defaultValue) { }
        public static void Look(ref bool value, string label, bool defaultValue) { }
    }
    public static class Messages
    {
        public static void Message(string text, RimWorld.MessageTypeDef type, bool historical) { }
    }
}

namespace RimWorld
{
    using Verse;
    public class Faction { public static Faction OfPlayer; }
    public class MessageTypeDef { }
    public static class MessageTypeDefOf
    {
        public static MessageTypeDef NeutralEvent;
        public static MessageTypeDef RejectInput;
        public static MessageTypeDef PositiveEvent;
    }
    public class Building_WorkTable : Building { }
    public class CompPowerTrader : ThingComp { public bool PowerOn; }
    public class CompFlickable : ThingComp { public bool SwitchIsOn; }
    public class CompProperties_Refuelable : CompProperties { public float fuelCapacity; }
    public class CompRefuelable : ThingComp
    {
        public float Fuel;
        public CompProperties_Refuelable Props;
        public void ConsumeFuel(float amount) { Fuel -= amount; }
    }
    public class RecipeWorker
    {
        public RecipeDef recipe;
        public virtual void Notify_IterationCompleted(Pawn billDoer, List<Thing> ingredients) { }
    }
}
