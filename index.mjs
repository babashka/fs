// Public JS API for @babashka/fs, compiled from src/babashka/fs.cljc by squint.
// Every function is exported under a friendly camelCase name; option maps accept
// camelCase (or dashed) keys.
// Generated from script/index.template.mjs by script/gen_index.cljs (bb gen-index).
import * as raw from './lib/babashka/fs.mjs';

// camelCase (or dashed) option keys are translated to the dashed keyword
// strings the squint-compiled functions read, e.g. {posixFilePermissions}
// and {visitFile} become {'posix-file-permissions'} / {'visit-file'}.
const kebabKey = (k) => k.replace(/[A-Z]/g, (m) => '-' + m.toLowerCase());
const isPlainObject = (x) =>
  x != null && typeof x === 'object' && Object.getPrototypeOf(x) === Object.prototype;
const kebabizeArg = (x) => {
  if (!isPlainObject(x)) return x;
  const out = {};
  for (const k of Object.keys(x)) out[kebabKey(k)] = x[k];
  return out;
};
const jsFriendly = (fn) => (...args) => fn(...args.map(kebabizeArg));

const isAbsolute = jsFriendly(raw.absolute_QMARK_);
const absolutize = jsFriendly(raw.absolutize);
const canonicalize = jsFriendly(raw.canonicalize);
const components = jsFriendly(raw.components);
const copy = jsFriendly(raw.copy);
const copyTree = jsFriendly(raw.copy_tree);
const createDir = jsFriendly(raw.create_dir);
const createDirs = jsFriendly(raw.create_dirs);
const createFile = jsFriendly(raw.create_file);
const createLink = jsFriendly(raw.create_link);
const createSymLink = jsFriendly(raw.create_sym_link);
const createTempDir = jsFriendly(raw.create_temp_dir);
const createTempFile = jsFriendly(raw.create_temp_file);
const creationTime = jsFriendly(raw.creation_time);
const cwd = jsFriendly(raw.cwd);
const delete_ = jsFriendly(raw.delete$);
const deleteIfExists = jsFriendly(raw.delete_if_exists);
const deleteOnExit = jsFriendly(raw.delete_on_exit);
const deleteTree = jsFriendly(raw.delete_tree);
const isDirectory = jsFriendly(raw.directory_QMARK_);
const endsWith = jsFriendly(raw.ends_with_QMARK_);
const execPaths = jsFriendly(raw.exec_paths);
const isExecutable = jsFriendly(raw.executable_QMARK_);
const exists = jsFriendly(raw.exists_QMARK_);
const expandHome = jsFriendly(raw.expand_home);
const extension = jsFriendly(raw.extension);
const file = jsFriendly(raw.file);
const fileName = jsFriendly(raw.file_name);
const fileSeparator = raw.file_separator;
const fileTimeToInstant = jsFriendly(raw.file_time__GT_instant);
const fileTimeToMillis = jsFriendly(raw.file_time__GT_millis);
const getAttribute = jsFriendly(raw.get_attribute);
const glob = jsFriendly(raw.glob);
const globToRegex = jsFriendly(raw.glob__GT_regex);
const gunzip = jsFriendly(raw.gunzip);
const gzip = jsFriendly(raw.gzip);
const isHidden = jsFriendly(raw.hidden_QMARK_);
const home = jsFriendly(raw.home);
const instantToFileTime = jsFriendly(raw.instant__GT_file_time);
const lastModifiedTime = jsFriendly(raw.last_modified_time);
const listDir = jsFriendly(raw.list_dir);
const listDirs = jsFriendly(raw.list_dirs);
const match = jsFriendly(raw.match);
const millisToFileTime = jsFriendly(raw.millis__GT_file_time);
const modifiedSince = jsFriendly(raw.modified_since);
const move = jsFriendly(raw.move);
const normalize = jsFriendly(raw.normalize);
const owner = jsFriendly(raw.owner);
const parent = jsFriendly(raw.parent);
const path = jsFriendly(raw.path);
const pathSeparator = raw.path_separator;
const posixToStr = jsFriendly(raw.posix__GT_str);
const posixFilePermissions = jsFriendly(raw.posix_file_permissions);
const readAllBytes = jsFriendly(raw.read_all_bytes);
const readAllLines = jsFriendly(raw.read_all_lines);
const readAttributes = jsFriendly(raw.read_attributes);
const readAttributesRaw = jsFriendly(raw.read_attributes_STAR_);
const readLink = jsFriendly(raw.read_link);
const isReadable = jsFriendly(raw.readable_QMARK_);
const realPath = jsFriendly(raw.real_path);
const isRegularFile = jsFriendly(raw.regular_file_QMARK_);
const isRelative = jsFriendly(raw.relative_QMARK_);
const relativize = jsFriendly(raw.relativize);
const root = jsFriendly(raw.root);
const isSameFile = jsFriendly(raw.same_file_QMARK_);
const setAttribute = jsFriendly(raw.set_attribute);
const setCreationTime = jsFriendly(raw.set_creation_time);
const setLastModifiedTime = jsFriendly(raw.set_last_modified_time);
const setPosixFilePermissions = jsFriendly(raw.set_posix_file_permissions);
const size = jsFriendly(raw.size);
const slurp = jsFriendly(raw.slurp);
const spit = jsFriendly(raw.spit);
const splitExt = jsFriendly(raw.split_ext);
const splitPaths = jsFriendly(raw.split_paths);
const startsWith = jsFriendly(raw.starts_with_QMARK_);
const strToPosix = jsFriendly(raw.str__GT_posix);
const stripExt = jsFriendly(raw.strip_ext);
const isSymlink = jsFriendly(raw.sym_link_QMARK_);
const tempDir = jsFriendly(raw.temp_dir);
const touch = jsFriendly(raw.touch);
const unixify = jsFriendly(raw.unixify);
const unzip = jsFriendly(raw.unzip);
const updateFile = jsFriendly(raw.update_file);
const walkFileTree = jsFriendly(raw.walk_file_tree);
const which = jsFriendly(raw.which);
const whichAll = jsFriendly(raw.which_all);
const isWindows = jsFriendly(raw.windows_QMARK_);
const isWritable = jsFriendly(raw.writable_QMARK_);
const writeBytes = jsFriendly(raw.write_bytes);
const writeLines = jsFriendly(raw.write_lines);
const xdgCacheHome = jsFriendly(raw.xdg_cache_home);
const xdgConfigHome = jsFriendly(raw.xdg_config_home);
const xdgDataHome = jsFriendly(raw.xdg_data_home);
const xdgStateHome = jsFriendly(raw.xdg_state_home);
const zip = jsFriendly(raw.zip);

export {
  isAbsolute,
  absolutize,
  canonicalize,
  components,
  copy,
  copyTree,
  createDir,
  createDirs,
  createFile,
  createLink,
  createSymLink,
  createTempDir,
  createTempFile,
  creationTime,
  cwd,
  delete_ as delete,
  deleteIfExists,
  deleteOnExit,
  deleteTree,
  isDirectory,
  endsWith,
  execPaths,
  isExecutable,
  exists,
  expandHome,
  extension,
  file,
  fileName,
  fileSeparator,
  fileTimeToInstant,
  fileTimeToMillis,
  getAttribute,
  glob,
  globToRegex,
  gunzip,
  gzip,
  isHidden,
  home,
  instantToFileTime,
  lastModifiedTime,
  listDir,
  listDirs,
  match,
  millisToFileTime,
  modifiedSince,
  move,
  normalize,
  owner,
  parent,
  path,
  pathSeparator,
  posixToStr,
  posixFilePermissions,
  readAllBytes,
  readAllLines,
  readAttributes,
  readAttributesRaw,
  readLink,
  isReadable,
  realPath,
  isRegularFile,
  isRelative,
  relativize,
  root,
  isSameFile,
  setAttribute,
  setCreationTime,
  setLastModifiedTime,
  setPosixFilePermissions,
  size,
  slurp,
  spit,
  splitExt,
  splitPaths,
  startsWith,
  strToPosix,
  stripExt,
  isSymlink,
  tempDir,
  touch,
  unixify,
  unzip,
  updateFile,
  walkFileTree,
  which,
  whichAll,
  isWindows,
  isWritable,
  writeBytes,
  writeLines,
  xdgCacheHome,
  xdgConfigHome,
  xdgDataHome,
  xdgStateHome,
  zip
};

// with-temp-dir is a compile-time macro; this is the JS-callable equivalent.
// Creates a temp dir, runs `callback(dir)`, then deletes it (unless `opts.keep`).
export function withTempDir(callback, opts) {
  const o = kebabizeArg(opts);
  const dir = raw.create_temp_dir(o);
  try {
    return callback(dir);
  } finally {
    if (!(o && o.keep)) {
      raw.delete_tree(dir, { force: true });
    }
  }
}
